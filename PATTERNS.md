# BookKeep Development Patterns

## Overview

This document outlines the development patterns, code organization, and best practices for the BookKeep project. These patterns align with the TypeScript-first, Firebase-native approach outlined in our architecture.

## Code Organization

### Project Structure
```
librarium/
├── src/
│   ├── components/           # Reusable UI components
│   │   ├── ui/              # Basic UI primitives (Button, Input, etc.)
│   │   ├── book/            # Book-specific components
│   │   ├── shelf/           # Shelf-related components
│   │   └── shared/          # Cross-domain components
│   ├── hooks/               # Custom React hooks
│   │   ├── useAuth.ts       # Authentication logic
│   │   ├── useBooks.ts      # Book data management
│   │   ├── useShelves.ts    # Shelf operations
│   │   └── useEvents.ts     # Event logging and history
│   ├── contexts/            # React Context providers
│   │   ├── AuthContext.tsx  # User authentication state
│   │   ├── ThemeContext.tsx # UI theme management
│   │   └── LibraryContext.tsx # Global library state
│   ├── lib/                 # Utility functions and services
│   │   ├── firebase/        # Firebase configuration and utils
│   │   ├── validation/      # Business logic validation
│   │   ├── api/            # External API integrations
│   │   └── utils/          # General utilities
│   ├── types/               # TypeScript type definitions
│   │   ├── book.ts         # Book-related types
│   │   ├── user.ts         # User and profile types
│   │   ├── shelf.ts        # Shelf and organization types
│   │   └── event.ts        # Event and history types
│   ├── pages/               # Next.js pages and API routes
│   │   ├── api/            # Next.js API routes
│   │   ├── auth/           # Authentication pages
│   │   ├── library/        # Library management pages
│   │   └── shelf/          # Shelf pages
│   └── styles/              # Global styles and Tailwind config
├── tests/                   # Test files
│   ├── __mocks__/          # Mock implementations
│   ├── components/         # Component tests
│   ├── hooks/              # Hook tests
│   └── lib/                # Utility function tests
├── docs/                    # Additional documentation
└── firebase/                # Firebase configuration
    ├── functions/          # Cloud Functions
    ├── firestore.rules     # Security rules
    └── firestore.indexes.json # Database indexes
```

## TypeScript Patterns

### Type-First Design

```typescript
// Define interfaces before implementation
interface BookFilters {
  state?: BookState
  isOwned?: boolean
  genre?: string[]
  author?: string
  dateRange?: {
    start: Date
    end: Date
  }
}

// Use utility types for variations
type CreateBookRequest = Omit<Book, 'id' | 'createdAt' | 'updatedAt'>
type UpdateBookRequest = Partial<Pick<Book, 'title' | 'author' | 'progress' | 'metadata'>>

// Discriminated unions for type safety
type BookEvent = 
  | { type: 'state_change'; data: { from: BookState; to: BookState } }
  | { type: 'progress_update'; data: { from: number; to: number } }
  | { type: 'comment'; data: { text: string; page?: number } }
  | { type: 'quote'; data: { text: string; page?: number } }
  | { type: 'review'; data: { rating: number; text: string } }
```

### Validation Functions

```typescript
// Pure validation functions instead of class methods
export const validateBookStateTransition = (
  from: BookState, 
  to: BookState
): { valid: boolean; error?: string } => {
  const validTransitions: Record<BookState, BookState[]> = {
    not_started: ['in_progress'],
    in_progress: ['finished', 'not_started'],
    finished: ['in_progress']
  }
  
  const isValid = validTransitions[from]?.includes(to) ?? false
  
  return {
    valid: isValid,
    error: isValid ? undefined : `Cannot transition from ${from} to ${to}`
  }
}

// Composable validation
export const validateBook = (book: Partial<Book>): ValidationResult => {
  const errors: string[] = []
  
  if (!book.title?.trim()) errors.push('Title is required')
  if (!book.author?.trim()) errors.push('Author is required')
  if (book.progress && (!book.metadata?.pages || book.progress > book.metadata.pages)) {
    errors.push('Progress cannot exceed total pages')
  }
  
  return {
    valid: errors.length === 0,
    errors
  }
}
```

### Factory Functions Over Builders

```typescript
// Factory function for creating books
export const createBook = (params: CreateBookParams): Book => {
  const now = new Date()
  
  return {
    id: '', // Will be set by Firestore
    title: params.title.trim(),
    author: params.author.trim(),
    isbn: params.isbn?.trim(),
    state: 'not_started',
    progress: 0,
    isOwned: params.isOwned ?? false,
    ownerId: params.ownerId,
    collaborators: params.collaborators || [],
    metadata: {
      pages: params.metadata?.pages,
      genre: params.metadata?.genre || [],
      publishedYear: params.metadata?.publishedYear,
      coverUrl: params.metadata?.coverUrl,
      description: params.metadata?.description
    },
    createdAt: now,
    updatedAt: now
  }
}

// Factory for events
export const createBookEvent = (
  bookId: string,
  userId: string,
  eventData: BookEventData
): BookEvent => ({
  id: '',
  bookId,
  userId,
  timestamp: new Date(),
  ...eventData
})
```

## React Patterns

### Custom Hooks for Data Management

```typescript
// Book management hook
export const useBooks = (userId: string, filters?: BookFilters) => {
  const [books, setBooks] = useState<Book[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)
  
  // Real-time subscription
  useEffect(() => {
    if (!userId) return
    
    const q = buildBooksQuery(userId, filters)
    
    const unsubscribe = onSnapshot(
      q,
      (snapshot) => {
        try {
          const booksData = snapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
          } as Book))
          
          setBooks(booksData)
          setLoading(false)
        } catch (err) {
          setError(err as Error)
          setLoading(false)
        }
      },
      (err) => {
        setError(err)
        setLoading(false)
      }
    )
    
    return unsubscribe
  }, [userId, filters])
  
  // Operations
  const addBook = useCallback(async (bookData: CreateBookRequest) => {
    const book = createBook({ ...bookData, ownerId: userId })
    const docRef = await addDoc(collection(firestore, `users/${userId}/books`), book)
    return docRef.id
  }, [userId])
  
  const updateBook = useCallback(async (bookId: string, updates: UpdateBookRequest) => {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await updateDoc(bookRef, { ...updates, updatedAt: new Date() })
  }, [userId])
  
  const deleteBook = useCallback(async (bookId: string) => {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await deleteDoc(bookRef)
  }, [userId])
  
  return {
    books,
    loading,
    error,
    operations: {
      addBook,
      updateBook,
      deleteBook
    }
  }
}

// Book state management hook
export const useBookState = (userId: string, bookId: string) => {
  const [book, setBook] = useState<Book | null>(null)
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    
    const unsubscribe = onSnapshot(bookRef, (doc) => {
      if (doc.exists()) {
        setBook({ id: doc.id, ...doc.data() } as Book)
      } else {
        setBook(null)
      }
      setLoading(false)
    })
    
    return unsubscribe
  }, [userId, bookId])
  
  const updateState = useCallback(async (newState: BookState) => {
    if (!book) return
    
    const validation = validateBookStateTransition(book.state, newState)
    if (!validation.valid) {
      throw new Error(validation.error)
    }
    
    await updateBookState(userId, bookId, newState, book.state)
  }, [userId, bookId, book])
  
  const updateProgress = useCallback(async (progress: number) => {
    if (!book || book.state !== 'in_progress') return
    
    await updateDoc(doc(firestore, `users/${userId}/books/${bookId}`), {
      progress,
      updatedAt: new Date()
    })
    
    // Log progress event
    await addDoc(collection(firestore, `users/${userId}/events`), {
      bookId,
      userId,
      type: 'progress_update',
      data: { from: book.progress || 0, to: progress },
      timestamp: new Date()
    })
  }, [userId, bookId, book])
  
  return {
    book,
    loading,
    operations: {
      updateState,
      updateProgress
    }
  }
}
```

### Component Composition Patterns

```typescript
// Compound component pattern for book display
export const BookCard = ({ book }: { book: Book }) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-4">
      <BookCard.Cover src={book.metadata.coverUrl} alt={book.title} />
      <BookCard.Content>
        <BookCard.Title>{book.title}</BookCard.Title>
        <BookCard.Author>{book.author}</BookCard.Author>
        <BookCard.State state={book.state} />
        {book.state === 'in_progress' && (
          <BookCard.Progress current={book.progress} total={book.metadata.pages} />
        )}
      </BookCard.Content>
      <BookCard.Actions>
        <BookCard.StateButton book={book} />
        <BookCard.MenuButton book={book} />
      </BookCard.Actions>
    </div>
  )
}

BookCard.Cover = ({ src, alt }: { src?: string; alt: string }) => (
  <div className="w-full h-48 bg-gray-200 rounded-md mb-3">
    {src ? (
      <img src={src} alt={alt} className="w-full h-full object-cover rounded-md" />
    ) : (
      <div className="w-full h-full flex items-center justify-center text-gray-400">
        No Cover
      </div>
    )}
  </div>
)

BookCard.Content = ({ children }: { children: React.ReactNode }) => (
  <div className="flex-1 space-y-2 mb-4">{children}</div>
)

BookCard.Title = ({ children }: { children: string }) => (
  <h3 className="font-semibold text-lg text-gray-900 line-clamp-2">{children}</h3>
)

BookCard.Author = ({ children }: { children: string }) => (
  <p className="text-gray-600 text-sm">{children}</p>
)

// Render props pattern for data fetching
export const BookProvider = ({ 
  userId, 
  bookId, 
  children 
}: { 
  userId: string
  bookId: string
  children: (props: { book: Book | null; loading: boolean }) => React.ReactNode 
}) => {
  const { book, loading } = useBookState(userId, bookId)
  
  return <>{children({ book, loading })}</>
}

// Usage
<BookProvider userId={user.id} bookId={bookId}>
  {({ book, loading }) => {
    if (loading) return <BookSkeleton />
    if (!book) return <BookNotFound />
    return <BookCard book={book} />
  }}
</BookProvider>
```

## Firebase Integration Patterns

### Service Layer Organization

```typescript
// lib/firebase/books.ts - Book operations
export const bookService = {
  // Get user's books with real-time updates
  subscribe: (userId: string, filters?: BookFilters, callback: (books: Book[]) => void) => {
    const q = buildBooksQuery(userId, filters)
    
    return onSnapshot(q, (snapshot) => {
      const books = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as Book))
      
      callback(books)
    })
  },
  
  // CRUD operations
  async create(userId: string, bookData: CreateBookRequest): Promise<string> {
    const book = createBook({ ...bookData, ownerId: userId })
    const docRef = await addDoc(collection(firestore, `users/${userId}/books`), book)
    return docRef.id
  },
  
  async update(userId: string, bookId: string, updates: UpdateBookRequest): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await updateDoc(bookRef, { ...updates, updatedAt: serverTimestamp() })
  },
  
  async delete(userId: string, bookId: string): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await deleteDoc(bookRef)
  },
  
  // State management
  async updateState(userId: string, bookId: string, newState: BookState, currentState: BookState): Promise<void> {
    const validation = validateBookStateTransition(currentState, newState)
    if (!validation.valid) {
      throw new Error(validation.error!)
    }
    
    const batch = writeBatch(firestore)
    
    // Update book
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    batch.update(bookRef, {
      state: newState,
      updatedAt: serverTimestamp(),
      ...(newState === 'in_progress' && { startedAt: serverTimestamp() }),
      ...(newState === 'finished' && { finishedAt: serverTimestamp() })
    })
    
    // Log event
    const eventRef = doc(collection(firestore, `users/${userId}/events`))
    batch.set(eventRef, {
      bookId,
      userId,
      type: 'state_change',
      data: { stateChange: { from: currentState, to: newState } },
      timestamp: serverTimestamp()
    })
    
    await batch.commit()
  }
}

// lib/firebase/events.ts - Event operations
export const eventService = {
  // Subscribe to book events
  subscribeToBookEvents: (userId: string, bookId: string, callback: (events: BookEvent[]) => void) => {
    const q = query(
      collection(firestore, `users/${userId}/events`),
      where('bookId', '==', bookId),
      orderBy('timestamp', 'desc')
    )
    
    return onSnapshot(q, (snapshot) => {
      const events = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as BookEvent))
      
      callback(events)
    })
  },
  
  // Add new event
  async addEvent(userId: string, eventData: Omit<BookEvent, 'id' | 'userId' | 'timestamp'>): Promise<void> {
    await addDoc(collection(firestore, `users/${userId}/events`), {
      ...eventData,
      userId,
      timestamp: serverTimestamp()
    })
  }
}
```

### Error Handling Patterns

```typescript
// Custom error types
export class BookError extends Error {
  constructor(message: string, public code: string) {
    super(message)
    this.name = 'BookError'
  }
}

export class ValidationError extends BookError {
  constructor(message: string, public field?: string) {
    super(message, 'VALIDATION_ERROR')
    this.name = 'ValidationError'
  }
}

// Error boundary component
export class BookErrorBoundary extends Component<
  { children: React.ReactNode; fallback?: React.ComponentType<{ error: Error }> },
  { hasError: boolean; error?: Error }
> {
  constructor(props: any) {
    super(props)
    this.state = { hasError: false }
  }
  
  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error }
  }
  
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Book operation error:', error, errorInfo)
    // Log to error tracking service
  }
  
  render() {
    if (this.state.hasError) {
      const FallbackComponent = this.props.fallback || DefaultErrorFallback
      return <FallbackComponent error={this.state.error!} />
    }
    
    return this.props.children
  }
}

// Hook for error handling
export const useErrorHandler = () => {
  const [error, setError] = useState<Error | null>(null)
  
  const handleError = useCallback((error: Error) => {
    console.error('Operation failed:', error)
    setError(error)
    
    // Show toast notification
    toast.error(error.message)
  }, [])
  
  const clearError = useCallback(() => {
    setError(null)
  }, [])
  
  return { error, handleError, clearError }
}
```

## Testing Patterns

### Unit Testing with Jest

```typescript
// tests/lib/validation.test.ts
import { validateBookStateTransition, validateBook } from '../lib/validation/book'

describe('Book Validation', () => {
  describe('validateBookStateTransition', () => {
    test('allows valid transitions', () => {
      expect(validateBookStateTransition('not_started', 'in_progress')).toEqual({
        valid: true
      })
    })
    
    test('rejects invalid transitions', () => {
      expect(validateBookStateTransition('not_started', 'finished')).toEqual({
        valid: false,
        error: 'Cannot transition from not_started to finished'
      })
    })
  })
  
  describe('validateBook', () => {
    test('validates required fields', () => {
      const result = validateBook({ title: '', author: 'Test Author' })
      
      expect(result.valid).toBe(false)
      expect(result.errors).toContain('Title is required')
    })
    
    test('validates progress against total pages', () => {
      const book = {
        title: 'Test Book',
        author: 'Test Author',
        progress: 150,
        metadata: { pages: 100 }
      }
      
      const result = validateBook(book)
      
      expect(result.valid).toBe(false)
      expect(result.errors).toContain('Progress cannot exceed total pages')
    })
  })
})
```

### Component Testing with React Testing Library

```typescript
// tests/components/BookCard.test.tsx
import { render, screen, fireEvent } from '@testing-library/react'
import { BookCard } from '../components/book/BookCard'
import { mockBook } from '../__mocks__/book'

describe('BookCard', () => {
  test('renders book information', () => {
    render(<BookCard book={mockBook} />)
    
    expect(screen.getByText(mockBook.title)).toBeInTheDocument()
    expect(screen.getByText(mockBook.author)).toBeInTheDocument()
  })
  
  test('shows progress for in-progress books', () => {
    const inProgressBook = { ...mockBook, state: 'in_progress', progress: 50 }
    
    render(<BookCard book={inProgressBook} />)
    
    expect(screen.getByRole('progressbar')).toBeInTheDocument()
  })
  
  test('handles state change button click', async () => {
    const onStateChange = jest.fn()
    
    render(<BookCard book={mockBook} onStateChange={onStateChange} />)
    
    fireEvent.click(screen.getByText('Start Reading'))
    
    expect(onStateChange).toHaveBeenCalledWith('in_progress')
  })
})
```

### Hook Testing

```typescript
// tests/hooks/useBooks.test.ts
import { renderHook, waitFor } from '@testing-library/react'
import { useBooks } from '../hooks/useBooks'
import { mockFirestore } from '../__mocks__/firebase'

jest.mock('../lib/firebase', () => mockFirestore)

describe('useBooks', () => {
  test('loads books for user', async () => {
    const { result } = renderHook(() => useBooks('user123'))
    
    expect(result.current.loading).toBe(true)
    
    await waitFor(() => {
      expect(result.current.loading).toBe(false)
      expect(result.current.books).toHaveLength(2)
    })
  })
  
  test('adds new book', async () => {
    const { result } = renderHook(() => useBooks('user123'))
    
    await waitFor(() => expect(result.current.loading).toBe(false))
    
    const newBook = { title: 'New Book', author: 'New Author' }
    const bookId = await result.current.operations.addBook(newBook)
    
    expect(bookId).toBeDefined()
    expect(result.current.books).toHaveLength(3)
  })
})
```

## Performance Patterns

### Memoization and Optimization

```typescript
// Memoized selectors
export const selectBooksByState = createSelector(
  [(books: Book[], state: BookState) => books, (books: Book[], state: BookState) => state],
  (books, state) => books.filter(book => book.state === state)
)

// Memoized components
export const BookCard = React.memo<BookCardProps>(({ book, onStateChange }) => {
  return (
    <div className="book-card">
      {/* Card content */}
    </div>
  )
}, (prevProps, nextProps) => {
  // Custom comparison for optimization
  return (
    prevProps.book.id === nextProps.book.id &&
    prevProps.book.updatedAt === nextProps.book.updatedAt
  )
})

// Debounced search
export const useSearchBooks = (query: string) => {
  const [results, setResults] = useState<Book[]>([])
  const [loading, setLoading] = useState(false)
  
  const debouncedQuery = useDebounce(query, 300)
  
  useEffect(() => {
    if (!debouncedQuery) {
      setResults([])
      return
    }
    
    setLoading(true)
    
    searchBooks(debouncedQuery)
      .then(setResults)
      .finally(() => setLoading(false))
  }, [debouncedQuery])
  
  return { results, loading }
}
```

### Virtual Scrolling for Large Lists

```typescript
import { FixedSizeList as List } from 'react-window'

export const VirtualBookList = ({ books }: { books: Book[] }) => {
  const Row = ({ index, style }: { index: number; style: React.CSSProperties }) => (
    <div style={style}>
      <BookCard book={books[index]} />
    </div>
  )
  
  return (
    <List
      height={600}
      itemCount={books.length}
      itemSize={200}
      width="100%"
    >
      {Row}
    </List>
  )
}
```

## Development Workflow

### Code Quality Tools

```json
// package.json scripts
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "lint:fix": "next lint --fix",
    "type-check": "tsc --noEmit",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage"
  }
}
```

### Git Hooks (Husky)

```json
// .husky/pre-commit
#!/usr/bin/env sh
. "$(dirname -- "$0")/_/husky.sh"

npm run lint
npm run type-check
npm run test
```

### Environment Setup

```typescript
// lib/env.ts - Environment validation
const envSchema = z.object({
  NEXT_PUBLIC_FIREBASE_API_KEY: z.string(),
  NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN: z.string(),
  NEXT_PUBLIC_FIREBASE_PROJECT_ID: z.string(),
  GOOGLE_BOOKS_API_KEY: z.string()
})

export const env = envSchema.parse(process.env)
```

These patterns provide a solid foundation for consistent, maintainable development practices that align with our TypeScript-first, Firebase-native architecture.