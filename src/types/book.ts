export type BookState = 'not_started' | 'in_progress' | 'finished'

export interface BookMetadata {
  pages?: number
  genre?: string[]
  publishedYear?: number
  coverUrl?: string
  description?: string
  rating?: number
  publisher?: string
  coverImage?: string
}

export interface Book {
  id: string
  title: string
  author: string
  isbn?: string
  state: BookState
  progress?: number
  isOwned: boolean
  ownerId: string
  collaborators?: string[]
  metadata: BookMetadata
  createdAt: Date
  updatedAt: Date
  startedAt?: Date
  finishedAt?: Date
}