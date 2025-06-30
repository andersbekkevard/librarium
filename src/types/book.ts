export type BookState = 'not_started' | 'in_progress' | 'finished'

export interface BookMetadata {
  pages?: number
  genre?: string[]
  publishedYear?: number
  coverUrl?: string
  description?: string
  rating?: number
  isbn?: string
}

export interface Book {
  id: string
  title: string
  author: string
  state: BookState
  progress?: number
  isOwned: boolean
  
  // Collaboration
  ownerId: string
  collaborators?: string[]
  sharedIn?: string[]
  
  // Metadata
  metadata: BookMetadata
  
  // Timestamps
  createdAt: Date
  updatedAt: Date
  startedAt?: Date
  finishedAt?: Date
}

export interface BookFilters {
  state?: BookState
  isOwned?: boolean
  genre?: string[]
  author?: string
  dateRange?: {
    start: Date
    end: Date
  }
}

// Request types
export type CreateBookRequest = Omit<Book, 'id' | 'createdAt' | 'updatedAt'>
export type UpdateBookRequest = Partial<Pick<Book, 'title' | 'author' | 'progress' | 'metadata' | 'state'>>

// Factory function parameters
export interface CreateBookParams {
  title: string
  author: string
  ownerId: string
  isOwned?: boolean
  collaborators?: string[]
  metadata?: Partial<BookMetadata>
}