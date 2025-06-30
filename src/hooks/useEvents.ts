'use client'

import { useState, useEffect, useCallback } from 'react'
import { BookEvent } from '@/types'
import { eventService } from '@/lib/services'

export const useBookEvents = (userId: string, bookId: string) => {
  const [events, setEvents] = useState<BookEvent[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    if (!userId || !bookId) {
      setEvents([])
      setLoading(false)
      return
    }

    try {
      const unsubscribe = eventService.subscribeToBookEvents(userId, bookId, (eventsData) => {
        const processedEvents = eventsData.map(event => ({
          ...event,
          timestamp: event.timestamp instanceof Date ? event.timestamp : event.timestamp.toDate()
        }))
        
        setEvents(processedEvents)
        setLoading(false)
        setError(null)
      })

      return unsubscribe
    } catch (err) {
      setError(err as Error)
      setLoading(false)
    }
  }, [userId, bookId])

  const addComment = useCallback(async (text: string, page?: number): Promise<string> => {
    try {
      return await eventService.logComment(userId, bookId, text, page)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId, bookId])

  const addQuote = useCallback(async (text: string, page?: number, chapter?: string): Promise<string> => {
    try {
      return await eventService.logQuote(userId, bookId, text, page, chapter)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId, bookId])

  const addReview = useCallback(async (rating: number, text: string, spoilerFree: boolean = true): Promise<string> => {
    try {
      return await eventService.logReview(userId, bookId, rating, text, spoilerFree)
    } catch (err) {
      setError(err as Error)
      throw err
    }
  }, [userId, bookId])

  return {
    events,
    loading,
    error,
    operations: {
      addComment,
      addQuote,
      addReview
    }
  }
}

export const useUserEvents = (userId: string, limit?: number) => {
  const [events, setEvents] = useState<BookEvent[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    if (!userId) {
      setEvents([])
      setLoading(false)
      return
    }

    try {
      const unsubscribe = eventService.subscribeToUserEvents(userId, (eventsData) => {
        const processedEvents = eventsData.map(event => ({
          ...event,
          timestamp: event.timestamp instanceof Date ? event.timestamp : event.timestamp.toDate()
        }))
        
        setEvents(processedEvents)
        setLoading(false)
        setError(null)
      }, limit)

      return unsubscribe
    } catch (err) {
      setError(err as Error)
      setLoading(false)
    }
  }, [userId, limit])

  return {
    events,
    loading,
    error
  }
}