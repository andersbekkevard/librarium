'use client'

import { useState, useEffect, useCallback } from 'react'
import { doc, onSnapshot } from 'firebase/firestore'
import { Book, BookState } from '@/types'
import { bookService } from '@/lib/services'
import { firestore } from '@/lib/firebase'

export const useBookState = (userId: string, bookId: string) => {
  const [book, setBook] = useState<Book | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  // Subscribe to individual book updates
  useEffect(() => {
    if (!userId || !bookId) {
      setBook(null)
      setLoading(false)
      return
    }

    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    
    const unsubscribe = onSnapshot(
      bookRef,
      (doc) => {
        if (doc.exists()) {
          const data = doc.data()
          const book: Book = {
            id: doc.id,
            ...data,
            createdAt: data.createdAt?.toDate() || new Date(),
            updatedAt: data.updatedAt?.toDate() || new Date(),
            startedAt: data.startedAt?.toDate(),
            finishedAt: data.finishedAt?.toDate()
          } as Book
          
          setBook(book)
        } else {
          setBook(null)
        }
        setLoading(false)
        setError(null)
      },
      (err) => {
        setError(err)
        setLoading(false)
      }
    )

    return unsubscribe
  }, [userId, bookId])

  // State management operations
  const updateState = useCallback(async (newState: BookState): Promise<void> => {
    if (!book) throw new Error('Book not loaded')

    try {
      await bookService.updateState(userId, bookId, newState, book.state)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId, bookId, book])

  const updateProgress = useCallback(async (progress: number): Promise<void> => {
    if (!book) throw new Error('Book not loaded')
    if (book.state !== 'in_progress') throw new Error('Can only update progress for books in progress')

    try {
      await bookService.updateProgress(userId, bookId, progress, book.progress || 0)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId, bookId, book])

  return {
    book,
    loading,
    error,
    operations: {
      updateState,
      updateProgress
    }
  }
}