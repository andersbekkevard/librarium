'use client'

import { useState, useEffect, useCallback } from 'react'
import { Book, BookFilters, CreateBookRequest, UpdateBookRequest, LoadingState } from '@/types'
import { bookService } from '@/lib/services'

export const useBooks = (userId: string, filters?: BookFilters) => {
  const [books, setBooks] = useState<Book[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  // Real-time subscription to books
  useEffect(() => {
    if (!userId) {
      setBooks([])
      setLoading(false)
      return
    }

    try {
      const unsubscribe = bookService.subscribe(userId, filters, (booksData) => {
        setBooks(booksData)
        setLoading(false)
        setError(null)
      })

      return unsubscribe
    } catch (err) {
      setError(err as Error)
      setLoading(false)
    }
  }, [userId, filters])

  // Book operations
  const addBook = useCallback(async (bookData: CreateBookRequest): Promise<string> => {
    try {
      const bookId = await bookService.create(userId, bookData)
      return bookId
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId])

  const updateBook = useCallback(async (bookId: string, updates: UpdateBookRequest): Promise<void> => {
    try {
      await bookService.update(userId, bookId, updates)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId])

  const deleteBook = useCallback(async (bookId: string): Promise<void> => {
    try {
      await bookService.delete(userId, bookId)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId])

  return {
    books,
    loading,
    error,
    operations: {
      addBook,
      updateBook,
      deleteBook
    }
  }
}