export interface UserProfile {
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

export interface UserStatistics {
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