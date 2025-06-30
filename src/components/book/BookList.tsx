'use client'

import React from 'react'
import { Book, BookState } from '@/types'
import { BookCard } from './BookCard'

interface BookListProps {
  books: Book[]
  loading?: boolean
  onStateChange?: (bookId: string, newState: BookState) => void
  onProgressUpdate?: (bookId: string, progress: number) => void
  emptyMessage?: string
}

const BookList = ({ 
  books, 
  loading = false, 
  onStateChange, 
  onProgressUpdate,
  emptyMessage = "No books found" 
}: BookListProps) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {Array.from({ length: 8 }).map((_, i) => (
          <BookSkeleton key={i} />
        ))}
      </div>
    )
  }

  if (books.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-12 text-center">
        <div className="text-muted-foreground text-lg mb-2">
          {emptyMessage}
        </div>
        <p className="text-sm text-muted-foreground">
          Start building your library by adding your first book!
        </p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {books.map((book) => (
        <BookCard
          key={book.id}
          book={book}
          onStateChange={(newState) => onStateChange?.(book.id, newState)}
          onProgressUpdate={(progress) => onProgressUpdate?.(book.id, progress)}
        />
      ))}
    </div>
  )
}

const BookSkeleton = () => (
  <div className="w-full max-w-sm">
    <div className="bg-card rounded-lg border p-4 animate-pulse">
      <div className="w-full h-48 bg-muted rounded-md mb-3" />
      <div className="space-y-2">
        <div className="h-6 bg-muted rounded w-3/4" />
        <div className="h-4 bg-muted rounded w-1/2" />
        <div className="h-6 bg-muted rounded w-1/3" />
      </div>
      <div className="mt-4">
        <div className="h-9 bg-muted rounded w-full" />
      </div>
    </div>
  </div>
)

export { BookList }