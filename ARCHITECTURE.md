# BookKeep Architecture

## Overview

BookKeep is built as a modern web application using **Firebase-native** patterns with **TypeScript-first** development. The architecture prioritizes real-time collaboration, type safety, and developer experience over complex abstractions.

## Design Principles

### 1. Firebase-Native Approach
- **Direct Integration**: Use Firebase SDK directly in React components via hooks
- **Real-time First**: Leverage Firestore listeners for live data synchronization
- **Serverless Functions**: Use Cloud Functions for external API integration and background tasks
- **Security by Rules**: Implement access control via Firestore security rules

### 2. TypeScript-First Development
- **Compile-time Safety**: Catch errors during development, not runtime
- **Interface-Driven Design**: Define clear contracts between components
- **Utility Functions**: Pure functions for business logic instead of class methods
- **Type Inference**: Leverage TypeScript's inference to reduce boilerplate

### 3. React Patterns
- **Hooks Over Classes**: Functional components with custom hooks for logic
- **Context for Global State**: React Context for user data and authentication
- **Component Composition**: Build complex UI from simple, reusable components
- **Separation of Concerns**: Presentational vs. container components

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Client (Next.js)                        │
├─────────────────────────────────────────────────────────────┤
│  UI Components (React + Tailwind)                          │
│  ├── Pages (Next.js routing)                               │
│  ├── Components (Reusable UI)                              │
│  └── Hooks (Data fetching & business logic)                │
├─────────────────────────────────────────────────────────────┤
│  State Management                                           │
│  ├── React Context (Auth, User data)                       │
│  ├── Local State (Component-specific)                      │
│  └── Firebase Listeners (Real-time data)                   │
├─────────────────────────────────────────────────────────────┤
│  Services Layer                                             │
│  ├── Firebase Utils (Firestore operations)                 │
│  ├── Validation Functions (Business logic)                 │
│  └── API Routes (Next.js server functions)                 │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    Firebase Backend                         │
├─────────────────────────────────────────────────────────────┤
│  Authentication (Firebase Auth)                             │
│  ├── Email/Password                                         │
│  ├── Social Providers (Google, GitHub)                     │
│  └── JWT Token Management                                   │
├─────────────────────────────────────────────────────────────┤
│  Database (Firestore)                                       │
│  ├── Real-time Listeners                                    │
│  ├── Security Rules                                         │
│  └── Automatic Indexing                                     │
├─────────────────────────────────────────────────────────────┤
│  Functions (Cloud Functions)                                │
│  ├── Google Books API Integration                           │
│  ├── Statistics Computation                                 │
│  └── Background Tasks                                       │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  External Services                          │
├─────────────────────────────────────────────────────────────┤
│  Google Books API (Metadata)                                │
│  Vercel/AWS (Hosting)                                       │
│  CDN (Static Assets)                                        │
└─────────────────────────────────────────────────────────────┘
```

## Database Design (Firestore)

### Schema Principles
- **User-Centric Partitioning**: Data organized under user documents for security and performance
- **Flexible Structure**: NoSQL design that can evolve with requirements
- **Real-time Optimized**: Structure supports efficient real-time queries
- **Collaboration-Ready**: Designed for multi-user access with permission controls

### Collection Structure

```
users/{userId}                          # User document
├── profile: UserProfile                # User profile data
├── statistics: UserStatistics          # Aggregated reading stats
├── books/{bookId}: Book                 # User's book collection
├── shelves/{shelfId}: Shelf             # Book organization
└── events/{eventId}: BookEvent          # Reading history/events

sharedShelves/{shelfId}                  # Top-level shared shelves
├── metadata: ShelfMetadata              # Shelf info and permissions
├── books/{bookId}: SharedBook           # Books in shared shelf
└── activity/{eventId}: ShelfEvent       # Shelf activity log
```

### Data Models

#### User Profile
```typescript
interface UserProfile {
  id: string
  email: string
  displayName: string
  photoURL?: string
  preferences: {
    theme: 'light' | 'dark' | 'system'
    defaultShelf: string
    privacyLevel: 'private' | 'friends' | 'public'
  }
  createdAt: Date
  lastActiveAt: Date
}
```

#### Book Entity
```typescript
interface Book {
  id: string
  title: string
  author: string
  isbn?: string
  state: 'not_started' | 'in_progress' | 'finished'
  progress?: number // Current page for in-progress books
  isOwned: boolean // true = owned, false = wishlist
  
  // Collaboration
  ownerId: string
  collaborators?: string[] // User IDs with access
  sharedIn?: string[] // Shelf IDs where this book appears
  
  // Metadata
  metadata: {
    pages?: number
    genre?: string[]
    publishedYear?: number
    coverUrl?: string
    description?: string
    rating?: number // User's personal rating
  }
  
  // Timestamps  
  createdAt: Date
  updatedAt: Date
  startedAt?: Date
  finishedAt?: Date
}
```

#### Book Event
```typescript
interface BookEvent {
  id: string
  bookId: string
  userId: string
  type: 'state_change' | 'progress_update' | 'comment' | 'quote' | 'review'
  timestamp: Date
  
  // Event-specific data
  data: {
    // State changes
    stateChange?: {
      from: BookState
      to: BookState
    }
    
    // Progress updates
    progressUpdate?: {
      from: number
      to: number
      pagesRead?: number
    }
    
    // User content
    comment?: {
      text: string
      page?: number
    }
    
    quote?: {
      text: string
      page?: number
      chapter?: string
    }
    
    review?: {
      rating: number
      text: string
      spoilerFree: boolean
    }
  }
}
```

#### Shelf Organization
```typescript
interface Shelf {
  id: string
  name: string
  description?: string
  bookIds: string[] // References to books
  
  // Ownership & Sharing
  ownerId: string
  collaborators: {
    userId: string
    permission: 'read' | 'write' | 'admin'
    addedAt: Date
  }[]
  
  // Visibility
  isPublic: boolean
  isDefault: boolean // e.g., "Currently Reading", "Wishlist"
  
  // Organization
  sortOrder: 'manual' | 'title' | 'author' | 'dateAdded' | 'dateRead'
  color?: string
  icon?: string
  
  createdAt: Date
  updatedAt: Date
}
```

#### User Statistics
```typescript
interface UserStatistics {
  userId: string
  
  // Reading Stats
  totalBooks: {
    owned: number
    wishlist: number
    finished: number
    inProgress: number
  }
  
  totalPages: number
  averageRating: number
  
  // Time-based stats
  readingStreaks: {
    current: number
    longest: number
    lastActivityDate: Date
  }
  
  // Goals
  yearlyGoals: {
    [year: string]: {
      target: number
      completed: number
      targetPages?: number
      pagesRead?: number
    }
  }
  
  // Insights
  genreBreakdown: Record<string, number>
  readingSpeed: {
    averagePagesPerDay: number
    averageDaysPerBook: number
  }
  
  // Social
  collaborativeBooks: number
  sharedShelves: number
  
  lastUpdated: Date
}
```

## Security Model

### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // User profile access
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // User's books
      match /books/{bookId} {
        allow read, write: if request.auth != null && (
          request.auth.uid == userId ||
          request.auth.uid in resource.data.collaborators
        );
      }
      
      // User's events (immutable after creation)
      match /events/{eventId} {
        allow read: if request.auth != null && request.auth.uid == userId;
        allow create: if request.auth != null && 
          request.auth.uid == userId &&
          request.resource.data.userId == userId;
        // No update or delete allowed (immutable events)
      }
      
      // User's shelves
      match /shelves/{shelfId} {
        allow read: if request.auth != null && (
          request.auth.uid == userId ||
          request.auth.uid in resource.data.collaborators.map(c => c.userId) ||
          resource.data.isPublic == true
        );
        
        allow write: if request.auth != null && (
          request.auth.uid == userId ||
          (request.auth.uid in resource.data.collaborators.map(c => c.userId) &&
           getCollaboratorPermission(request.auth.uid, resource.data.collaborators) in ['write', 'admin'])
        );
      }
    }
    
    // Shared shelves (top-level collection)
    match /sharedShelves/{shelfId} {
      allow read: if request.auth != null && (
        request.auth.uid == resource.data.ownerId ||
        request.auth.uid in resource.data.collaborators.map(c => c.userId) ||
        resource.data.isPublic == true
      );
      
      allow write: if request.auth != null && (
        request.auth.uid == resource.data.ownerId ||
        (request.auth.uid in resource.data.collaborators.map(c => c.userId) &&
         getCollaboratorPermission(request.auth.uid, resource.data.collaborators) in ['write', 'admin'])
      );
      
      // Books in shared shelves
      match /books/{bookId} {
        allow read, write: if request.auth != null && (
          request.auth.uid == get(/databases/$(database)/documents/sharedShelves/$(shelfId)).data.ownerId ||
          request.auth.uid in get(/databases/$(database)/documents/sharedShelves/$(shelfId)).data.collaborators.map(c => c.userId)
        );
      }
    }
    
    // Helper function for permission checking
    function getCollaboratorPermission(userId, collaborators) {
      return collaborators.filter(c => c.userId == userId)[0].permission;
    }
  }
}
```

### Authentication Strategy

```typescript
// Authentication context
interface AuthContextType {
  user: User | null
  loading: boolean
  signIn: (email: string, password: string) => Promise<void>
  signUp: (email: string, password: string, displayName: string) => Promise<void>
  signInWithGoogle: () => Promise<void>
  signOut: () => Promise<void>
}

// Usage in components
const { user, loading } = useAuth()

if (loading) return <LoadingSpinner />
if (!user) return <LoginPage />

// User is authenticated, render app
return <BookLibrary />
```

## Data Access Patterns

### Custom Hooks for Data Fetching

```typescript
// Real-time books hook
const useBooks = (userId: string, filters?: BookFilters) => {
  const [books, setBooks] = useState<Book[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  
  useEffect(() => {
    if (!userId) return
    
    let q = collection(firestore, `users/${userId}/books`)
    
    // Apply filters
    if (filters?.state) {
      q = query(q, where('state', '==', filters.state))
    }
    if (filters?.isOwned !== undefined) {
      q = query(q, where('isOwned', '==', filters.isOwned))
    }
    
    const unsubscribe = onSnapshot(
      q,
      (snapshot) => {
        const booksData = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        } as Book))
        
        setBooks(booksData)
        setLoading(false)
      },
      (err) => {
        setError(err.message)
        setLoading(false)
      }
    )
    
    return unsubscribe
  }, [userId, filters])
  
  return { books, loading, error }
}

// Collaborative books hook
const useSharedBooks = (userId: string) => {
  const [books, setBooks] = useState<Book[]>([])
  
  useEffect(() => {
    // Query across all user collections where current user is collaborator
    const q = query(
      collectionGroup(firestore, 'books'),
      where('collaborators', 'array-contains', userId)
    )
    
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const sharedBooks = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as Book))
      
      setBooks(sharedBooks)
    })
    
    return unsubscribe
  }, [userId])
  
  return books
}
```

### Service Functions for Operations

```typescript
// Book state management
export const updateBookState = async (
  userId: string,
  bookId: string,
  newState: BookState,
  currentState: BookState
): Promise<void> => {
  // Validation
  if (!canTransitionTo(currentState, newState)) {
    throw new Error(`Invalid state transition: ${currentState} → ${newState}`)
  }
  
  const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
  const eventRef = doc(collection(firestore, `users/${userId}/events`))
  
  // Use batch for atomic operations
  const batch = writeBatch(firestore)
  
  // Update book state
  batch.update(bookRef, {
    state: newState,
    updatedAt: serverTimestamp(),
    ...(newState === 'in_progress' && { startedAt: serverTimestamp() }),
    ...(newState === 'finished' && { finishedAt: serverTimestamp() })
  })
  
  // Log state change event
  batch.set(eventRef, {
    bookId,
    userId,
    type: 'state_change',
    data: {
      stateChange: { from: currentState, to: newState }
    },
    timestamp: serverTimestamp()
  })
  
  await batch.commit()
}

// State transition validation
const canTransitionTo = (from: BookState, to: BookState): boolean => {
  const validTransitions: Record<BookState, BookState[]> = {
    not_started: ['in_progress'],
    in_progress: ['finished', 'not_started'], // Allow restart
    finished: ['in_progress'] // Allow re-reading
  }
  
  return validTransitions[from]?.includes(to) ?? false
}

// Book search and creation
export const searchAndCreateBook = async (
  userId: string,
  searchQuery: string
): Promise<Book[]> => {
  // Call Next.js API route for Google Books search
  const response = await fetch(`/api/books/search?q=${encodeURIComponent(searchQuery)}`)
  const { books: searchResults } = await response.json()
  
  return searchResults.map(book => ({
    ...book,
    id: '', // Will be set by Firestore
    state: 'not_started' as BookState,
    isOwned: false, // Default to wishlist
    ownerId: userId,
    createdAt: new Date(),
    updatedAt: new Date()
  }))
}
```

## Performance Optimization

### Query Optimization
- **Compound Indexes**: Create indexes for common filter combinations
- **Pagination**: Use `startAfter` for large collections
- **Field Selection**: Query only needed fields for list views
- **Caching**: Leverage Firebase offline persistence

### UI Performance
- **Virtualization**: Use React Window for large book lists
- **Lazy Loading**: Load book covers and metadata on demand
- **Memoization**: React.memo for expensive components
- **Code Splitting**: Dynamic imports for route-based splitting

### Real-time Optimization
- **Selective Listening**: Subscribe only to needed data
- **Listener Cleanup**: Proper unsubscribe in useEffect cleanup
- **Batched Updates**: Group multiple Firestore operations
- **Optimistic Updates**: Update UI before Firebase confirms

## Deployment Strategy

### Environment Configuration
```typescript
// next.config.js
module.exports = {
  env: {
    NEXT_PUBLIC_FIREBASE_API_KEY: process.env.FIREBASE_API_KEY,
    NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN: process.env.FIREBASE_AUTH_DOMAIN,
    NEXT_PUBLIC_FIREBASE_PROJECT_ID: process.env.FIREBASE_PROJECT_ID,
    GOOGLE_BOOKS_API_KEY: process.env.GOOGLE_BOOKS_API_KEY // Server-side only
  }
}
```

### CI/CD Pipeline
1. **Development**: Local development with Firebase emulators
2. **Staging**: Automated deployment to staging environment on PR
3. **Production**: Deploy to production on main branch merge
4. **Monitoring**: Error tracking with Sentry, performance monitoring

### Scaling Considerations
- **Firebase Pricing**: Monitor read/write operations and optimize queries
- **CDN**: Use Next.js static generation for public pages
- **Database Limits**: Plan for Firestore document size and collection limits
- **Function Timeouts**: Design Cloud Functions for appropriate execution time

## Testing Strategy

### Unit Testing
```typescript
// Example test for validation function
describe('Book State Transitions', () => {
  test('should allow transition from not_started to in_progress', () => {
    expect(canTransitionTo('not_started', 'in_progress')).toBe(true)
  })
  
  test('should not allow transition from finished to not_started', () => {
    expect(canTransitionTo('finished', 'not_started')).toBe(false)
  })
})
```

### Integration Testing
- **Firebase Emulator**: Test Firestore rules and operations
- **API Testing**: Test Next.js API routes with mock data
- **Real-time Testing**: Verify listener behavior and updates

### E2E Testing
- **Cypress**: Test critical user journeys
- **Authentication Flow**: Login/signup testing
- **Book Management**: Add, update, delete operations
- **Collaboration**: Multi-user scenarios

## Monitoring & Observability

### Application Monitoring
- **Error Tracking**: Sentry for error monitoring
- **Performance**: Web Vitals and custom metrics
- **User Analytics**: Firebase Analytics for usage patterns

### Infrastructure Monitoring
- **Firebase Usage**: Monitor Firestore reads/writes and costs
- **Function Performance**: Cloud Function execution times and errors
- **API Rate Limits**: Google Books API usage tracking

This architecture provides a solid foundation for a scalable, maintainable BookKeep application that embraces modern web development practices while leveraging Firebase's real-time capabilities effectively.