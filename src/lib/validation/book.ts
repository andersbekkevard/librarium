import { BookState, ValidationResult, Book } from '@/types'

export const validateBookStateTransition = (
  from: BookState, 
  to: BookState
): ValidationResult => {
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

export const validateBook = (book: Partial<Book>): ValidationResult => {
  const errors: string[] = []
  
  if (!book.title?.trim()) errors.push('Title is required')
  if (!book.author?.trim()) errors.push('Author is required')
  
  if (book.progress !== undefined) {
    if (book.progress < 0) {
      errors.push('Progress cannot be negative')
    }
    if (book.metadata?.pages && book.progress > book.metadata.pages) {
      errors.push('Progress cannot exceed total pages')
    }
  }
  
  if (book.metadata?.rating !== undefined) {
    if (book.metadata.rating < 1 || book.metadata.rating > 5) {
      errors.push('Rating must be between 1 and 5')
    }
  }
  
  if (book.metadata?.pages !== undefined && book.metadata.pages <= 0) {
    errors.push('Page count must be positive')
  }
  
  if (book.metadata?.publishedYear !== undefined) {
    const currentYear = new Date().getFullYear()
    if (book.metadata.publishedYear < 0 || book.metadata.publishedYear > currentYear + 1) {
      errors.push('Published year is invalid')
    }
  }
  
  return {
    valid: errors.length === 0,
    errors: errors.length > 0 ? errors : undefined
  }
}

export const validateProgress = (currentProgress: number, totalPages?: number): ValidationResult => {
  const errors: string[] = []
  
  if (currentProgress < 0) {
    errors.push('Progress cannot be negative')
  }
  
  if (totalPages !== undefined && currentProgress > totalPages) {
    errors.push('Progress cannot exceed total pages')
  }
  
  return {
    valid: errors.length === 0,
    errors: errors.length > 0 ? errors : undefined
  }
}