import type { Book, BookState } from './book'

export interface BookEvent {
  id: string
  bookId: string
  userId: string
  type: 'state_change' | 'progress_update' | 'comment' | 'quote' | 'review' | 'rating'
  timestamp: Date
  data: {
    stateChange?: { from: BookState; to: BookState }
    progressUpdate?: { from: number; to: number; pagesRead?: number }
    comment?: { text: string; page?: number; spoilerFree?: boolean }
    quote?: { text: string; page?: number; chapter?: string }
    review?: { text: string; spoilerFree?: boolean }
    rating?: { rating: number; previousRating?: number }
  }
}

export interface ReadingGoal {
  id: string
  userId: string
  title: string
  description?: string
  targetType: 'books' | 'pages' | 'minutes'
  targetValue: number
  currentValue: number
  startDate: Date
  endDate: Date
  status: 'active' | 'completed' | 'paused' | 'failed'
  createdAt: Date
}

export interface UserStatistics {
  userId: string
  totalBooks: {
    owned: number
    wishlist: number
    finished: number
    inProgress: number
    notStarted: number
  }
  totalPages: number
  averageRating: number
  readingStreaks: {
    current: number
    longest: number
    lastActivityDate: Date
  }
  yearlyStats: {
    [year: string]: {
      booksRead: number
      pagesRead: number
      averageRating: number
      favoriteGenres: string[]
    }
  }
  genreBreakdown: Record<string, number>
  readingSpeed: {
    averagePagesPerDay: number
    averageDaysPerBook: number
  }
  monthlyProgress: {
    [month: string]: {
      booksCompleted: number
      pagesRead: number
    }
  }
}

export interface Shelf {
  id: string
  name: string
  description?: string
  bookIds: string[]
  ownerId: string
  collaborators: {
    userId: string
    permission: 'read' | 'write' | 'admin'
    addedAt: Date
  }[]
  isPublic: boolean
  isDefault: boolean
  color?: string
  icon?: string
  createdAt: Date
  updatedAt: Date
}

export interface Household {
  id: string
  name: string
  description?: string
  members: {
    userId: string
    name: string
    email: string
    role: 'owner' | 'admin' | 'member'
    joinedAt: Date
  }[]
  sharedBooks: string[]
  location: string
  settings: {
    allowBookLending: boolean
    requireApprovalForNewMembers: boolean
    defaultBookVisibility: 'private' | 'household' | 'public'
  }
  createdAt: Date
}

export interface Collaboration {
  id: string
  bookId: string
  ownerId: string
  collaboratorId: string
  permission: 'read' | 'write'
  status: 'pending' | 'accepted' | 'rejected'
  invitedAt: Date
  respondedAt?: Date
}

export interface SearchFilters {
  query?: string
  states?: BookState[]
  genres?: string[]
  authors?: string[]
  ratings?: number[]
  yearRange?: { start: number; end: number }
  owned?: boolean
  shelves?: string[]
  collaborators?: string[]
}

export interface DashboardData {
  recentActivity: BookEvent[]
  currentlyReading: Book[]
  recentlyFinished: Book[]
  upcomingGoals: ReadingGoal[]
  quickStats: {
    booksThisMonth: number
    pagesThisWeek: number
    currentStreak: number
    completionRate: number
  }
}