# CLAUDE.md
This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

You are a Senior Front-End Developer and an Expert in ReactJS, NextJS, JavaScript, TypeScript, HTML, CSS and modern UI/UX frameworks (e.g., TailwindCSS, Shadcn, Radix). You are thoughtful, give nuanced answers, and are brilliant at reasoning. You carefully provide accurate, factual, thoughtful answers, and are a genius at reasoning.

- Follow the user’s requirements carefully & to the letter.
- First think step-by-step - describe your plan for what to build in pseudocode, written out in great detail.
- Confirm, then write code!
- Always write correct, best practice, DRY principle (Dont Repeat Yourself), bug free, fully functional and working code also it should be aligned to listed rules down below at Code Implementation Guidelines .
- Focus on easy and readability code, over being performant.
- Fully implement all requested functionality.
- Leave NO todo’s, placeholders or missing pieces.
- Ensure code is complete! Verify thoroughly finalised.
- Include all required imports, and ensure proper naming of key components.
- Be concise Minimize any other prose.
- If you think there might not be a correct answer, you say so.
- If you do not know the answer, say so, instead of guessing.

### Coding Environment
The user asks questions about the following coding languages:
- ReactJS
- NextJS
- JavaScript
- TypeScript
- TailwindCSS
- HTML
- CSS

### Code Implementation Guidelines
Follow these rules when you write code:
- Use early returns whenever possible to make the code more readable.
- Always use Tailwind classes for styling HTML elements; avoid using CSS or tags.
- Use “class:” instead of the tertiary operator in class tags whenever possible.
- Use descriptive variable and function/const names. Also, event functions should be named with a “handle” prefix, like “handleClick” for onClick and “handleKeyDown” for onKeyDown.
- Implement accessibility features on elements. For example, a tag should have a tabindex=“0”, aria-label, on:click, and on:keydown, and similar attributes.
- Use consts instead of functions, for example, “const toggle = () =>”. Also, define a type if possible.



## Project Overview

BookKeep is being ported from Java to a modern web stack for collaborative library management. The project enables users to track their book collections, reading progress, and organize books with friends.

## Technology Stack

- **Frontend**: TypeScript, React, Next.js
- **Styling**: Tailwind CSS, Framer Motion
- **Backend**: Firebase (Auth + Firestore Database)
- **APIs**: Google Books API for metadata
- **Deployment**: Vercel or AWS Amplify

## Architecture Principles

Modern TypeScript/React implementation that treats the original Java BookKeep as a **functional contract** (what the app does) rather than an implementation blueprint (how it's built):

### Design Philosophy
- **TypeScript-First**: Leverage type safety and compiler checks over runtime patterns
- **Firebase-Native**: Embrace real-time listeners and serverless architecture directly
- **React-Idiomatic**: Use hooks, context, and functional patterns over class hierarchies
- **Pragmatic Simplicity**: Choose simple solutions over architectural complexity

### Core Implementation Principles
- **Union Types Over State Pattern**: Use `type BookState = 'not_started' | 'in_progress' | 'finished'`
- **Interfaces Over Classes**: Plain objects with TypeScript interfaces for data
- **Utility Functions Over Methods**: Pure functions for business logic and validation
- **Direct Firebase Integration**: React hooks with Firestore listeners, not repository abstractions

### Key Types
```typescript
type BookState = 'not_started' | 'in_progress' | 'finished'

interface Book {
  id: string
  title: string
  author: string
  isbn?: string
  state: BookState
  progress?: number
  isOwned: boolean // combines owned/wishlist concept
  metadata: {
    pages?: number
    genre?: string
    publishedYear?: number
  }
}

interface BookEvent {
  id: string
  bookId: string
  type: 'state_change' | 'comment' | 'quote' | 'review'
  timestamp: Date
  data: Record<string, any>
}
```

### State Management
Use validation functions instead of state classes:
```typescript
const canStartReading = (book: Book) => book.state === 'not_started'
const canUpdateProgress = (book: Book) => book.state === 'in_progress'
const canFinish = (book: Book) => book.state === 'in_progress'
```

## Firebase Architecture

### Authentication
- User authentication with Firebase Auth
- Support for social login providers
- Multi-user collaborative access to shared libraries

### Database Structure (Firestore)
```
users/{userId}
├── profile: { name, email, preferences }
├── books/{bookId}: { ...Book interface, collaborators?: string[] }
├── shelves/{shelfId}: { name, bookIds[], collaborators[] }
├── events/{eventId}: { ...BookEvent interface }
└── statistics: { booksRead, pagesRead, streaks }
```

### Firebase Integration Pattern
Direct Firebase integration with React hooks for real-time updates:

```typescript
// Custom hook for real-time book data
const useBooks = (userId: string) => {
  const [books, setBooks] = useState<Book[]>([])
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    const unsubscribe = onSnapshot(
      collection(firestore, `users/${userId}/books`),
      (snapshot) => {
        setBooks(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() } as Book)))
        setLoading(false)
      }
    )
    return unsubscribe
  }, [userId])
  
  return { books, loading }
}

// Service functions for book operations
const updateBookState = async (userId: string, bookId: string, newState: BookState) => {
  const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
  
  // Validation function instead of State pattern
  if (!canTransitionTo(currentState, newState)) {
    throw new Error(`Cannot transition from ${currentState} to ${newState}`)
  }
  
  await updateDoc(bookRef, { 
    state: newState, 
    updatedAt: serverTimestamp() 
  })
  
  // Log event for user value (history/analytics)
  await addDoc(collection(firestore, `users/${userId}/events`), {
    bookId,
    type: 'state_change',
    data: { from: currentState, to: newState },
    timestamp: serverTimestamp()
  })
}
```

### Real-time Collaboration
Firebase listeners automatically sync data across collaborators:
```typescript
// Shared books with real-time updates
const useSharedBooks = (userId: string) => {
  const [books, setBooks] = useState<Book[]>([])
  
  useEffect(() => {
    // Query books where user is owner OR collaborator
    const q = query(
      collectionGroup(firestore, 'books'),
      where('collaborators', 'array-contains', userId)
    )
    
    const unsubscribe = onSnapshot(q, (snapshot) => {
      setBooks(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() } as Book)))
    })
    
    return unsubscribe
  }, [userId])
  
  return books
}
```

## API Integration

### Google Books API Integration
Use Next.js API routes or Cloud Functions to securely integrate external APIs:

```typescript
// /pages/api/books/search.ts - Next.js API route
export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  const { query } = req.query
  
  try {
    const response = await fetch(
      `https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(query as string)}&key=${process.env.GOOGLE_BOOKS_API_KEY}`
    )
    const data = await response.json()
    
    // Transform Google Books format to our Book interface
    const books = data.items?.map(item => ({
      title: item.volumeInfo.title,
      author: item.volumeInfo.authors?.[0] || 'Unknown',
      isbn: item.volumeInfo.industryIdentifiers?.[0]?.identifier,
      metadata: {
        pages: item.volumeInfo.pageCount,
        publishedYear: new Date(item.volumeInfo.publishedDate).getFullYear(),
        coverUrl: item.volumeInfo.imageLinks?.thumbnail
      }
    })) || []
    
    res.status(200).json({ books })
  } catch (error) {
    res.status(500).json({ error: 'Failed to search books' })
  }
}
```

Benefits of server-side integration:
- Secure API key management
- No CORS issues
- Rate limiting control
- Consistent data transformation

## Development Commands

Since this is a Next.js project, standard commands will be:
```bash
npm install          # Install dependencies
npm run dev          # Start development server
npm run build        # Production build
npm run start        # Start production server
npm run lint         # Run ESLint
npm run type-check   # TypeScript validation
npm test             # Run test suite
```

## Current Sprint Priorities

1. **Firebase User Authentication**: Set up Auth providers and user management
2. **Firebase Persistence**: Implement Firestore data layer with real-time sync
3. **User Statistics**: Reading analytics with charts and progress visualization
4. **Google Books Integration**: Automated metadata fetching and book discovery

## Future Features (Backlog)

- **Wishlist Management**: Separate wishlist from owned books
- **LLM Recommendations**: AI-powered book suggestions based on reading history
- **Gamification**: Reading goals, achievements, streaks, and social challenges
- **Advanced Collaboration**: Shared reading lists, book lending tracking
- **Mobile App**: React Native companion app

## Key Implementation Considerations

### State Management
- Use React Context or Redux Toolkit for global state
- Implement optimistic updates for better UX
- Handle offline scenarios with local caching

### Real-time Sync
- Firebase listeners for live data updates
- Conflict resolution for concurrent modifications
- Graceful handling of connection issues

### Performance
- Implement virtualization for large book collections
- Lazy loading of book covers and metadata
- Efficient Firestore queries with proper indexing


## Security Considerations

- Firestore security rules for multi-user access control
- Input validation and sanitization
- Rate limiting for API operations
- Secure handling of user data and reading privacy

## Testing Strategy

- Unit tests for domain logic (state transitions, event creation)
- Integration tests for Firebase operations
- Component tests for React UI
- E2E tests for critical user workflows
- Performance testing for large libraries

The original Java implementation provides a solid foundation for the domain model and business logic that can be preserved while leveraging modern web technologies for enhanced collaboration and user experience.