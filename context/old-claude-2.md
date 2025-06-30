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



This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BookKeep is being ported from a Java desktop application to a modern web application using Next.js, TypeScript, Firebase, and Tailwind CSS. The goal is to create a collaborative reading tracker that allows users to manage their book collections, track reading progress, and share literary experiences with friends.

## Technology Stack

- **Frontend**: Next.js 14+, React 18+, TypeScript 5+
- **Styling**: Tailwind CSS, Radix UI components  
- **Backend**: Firebase (Auth + Firestore + Functions)
- **External APIs**: Google Books API for metadata
- **Deployment**: Vercel

## Current Sprint Goals
0. Set up necessary classes
1. Firebase user authentication
2. Firebase persistence layer
3. User statistics with reading progress visualization
4. Google Books API integration for book metadata

## Architecture Principles

### Firebase-Native Approach
- Use Firebase SDK directly in React components via hooks
- Leverage Firestore listeners for real-time data synchronization
- Implement access control via Firestore security rules
- Use Cloud Functions for external API integration

### TypeScript-First Development
- Define interfaces before implementation
- Use utility types for variations (Omit, Pick, Partial)
- Leverage discriminated unions for type safety
- Prefer pure functions over class methods for business logic

### React Patterns
- Functional components with custom hooks for logic
- React Context for global state (auth, library data)
- Component composition over complex inheritance
- Separation of presentational vs. container components

## Key Data Models

### Core Book Entity
```typescript
interface Book {
  id: string
  title: string
  author: string
  isbn?: string
  state: 'not_started' | 'in_progress' | 'finished'
  progress?: number
  isOwned: boolean // true for owned, false for wishlist
  ownerId: string
  collaborators?: string[]
  metadata: {
    pages?: number
    genre?: string[]
    publishedYear?: number
    coverUrl?: string
    description?: string
  }
  createdAt: Date
  updatedAt: Date
}
```

### Event Logging
```typescript
interface BookEvent {
  id: string
  bookId: string
  userId: string
  type: 'state_change' | 'progress_update' | 'comment' | 'quote' | 'review'
  timestamp: Date
  data: {
    stateChange?: { from: BookState; to: BookState }
    progressUpdate?: { from: number; to: number }
    comment?: { text: string; page?: number }
    quote?: { text: string; page?: number }
    review?: { rating: number; text: string }
  }
}
```

## Firestore Schema Structure

```
users/{userId}
├── profile: UserProfile
├── statistics: UserStatistics  
├── books/{bookId}: Book
├── shelves/{shelfId}: Shelf
└── events/{eventId}: BookEvent

sharedShelves/{shelfId}
├── metadata: ShelfMetadata
├── books/{bookId}: SharedBook
└── activity/{eventId}: ShelfEvent
```

## Development Patterns

### State Management
- Use React Context for user authentication and global library state
- Custom hooks for data fetching with Firestore listeners
- Local component state for UI-specific data (forms, modals)

### Data Operations
- Create service functions for Firestore operations instead of heavy Repository classes
- Use factory functions over Builder patterns for object creation
- Implement validation with pure functions, not class methods

### Real-time Updates
- Subscribe to Firestore collections via onSnapshot
- Update UI optimistically where appropriate
- Handle offline scenarios with Firebase offline persistence

### Error Handling
- Use custom error types for domain-specific errors
- Implement error boundaries for React components
- Provide user-friendly error messages and recovery options

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── ui/             # Basic primitives (Button, Input)
│   ├── book/           # Book-specific components
│   └── shelf/          # Shelf-related components
├── hooks/              # Custom React hooks
├── contexts/           # React Context providers
├── lib/                # Utility functions and services
│   ├── firebase/       # Firebase configuration and utils
│   ├── validation/     # Business logic validation
│   └── api/           # External API integrations
├── types/              # TypeScript type definitions
├── pages/              # Next.js pages and API routes
└── styles/             # Global styles and Tailwind config
```

## Development Commands

Since this is a new project being set up, standard Next.js commands will apply:

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Run type checking
npm run type-check

# Run linting
npm run lint

# Run tests
npm run test
```

## Firebase Setup

- Authentication providers: Email/password, Google, GitHub
- Firestore security rules enforce user permissions and data integrity
- Cloud Functions handle Google Books API integration server-side
- Real-time listeners provide collaborative features

## Testing Strategy

- Unit tests for validation functions and business logic
- Component tests with React Testing Library
- Hook tests for custom data fetching hooks
- Firebase emulator for testing Firestore rules and operations


## Future Features

- Wishlist management separate from owned books
- LLM-based book recommendations
- Gamification with reading goals and achievements
- Mobile app with React Native
- Social features and public profiles