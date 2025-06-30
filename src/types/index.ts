// Re-export all types from individual modules
export * from './book'
export * from './user'
export * from './shelf'
export * from './event'

// Common utility types
export interface PaginationOptions {
  limit?: number
  startAfter?: any
}

export interface SearchOptions {
  query: string
  limit?: number
  filters?: Record<string, any>
}

export interface ApiResponse<T> {
  data: T
  error?: string
  message?: string
}

export interface LoadingState {
  loading: boolean
  error: Error | null
}