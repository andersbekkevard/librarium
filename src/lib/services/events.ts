import { 
  collection, 
  addDoc, 
  query, 
  where, 
  orderBy, 
  onSnapshot,
  serverTimestamp 
} from 'firebase/firestore'
import { firestore } from '@/lib/firebase'
import { BookEvent, BookEventData } from '@/types'

export const eventService = {
  // Subscribe to book events
  subscribeToBookEvents: (
    userId: string, 
    bookId: string, 
    callback: (events: BookEvent[]) => void
  ): (() => void) => {
    const q = query(
      collection(firestore, `users/${userId}/events`),
      where('bookId', '==', bookId),
      orderBy('timestamp', 'desc')
    )
    
    return onSnapshot(q, (snapshot) => {
      const events = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as BookEvent))
      
      callback(events)
    })
  },

  // Subscribe to all user events
  subscribeToUserEvents: (
    userId: string,
    callback: (events: BookEvent[]) => void,
    limit?: number
  ): (() => void) => {
    let q = query(
      collection(firestore, `users/${userId}/events`),
      orderBy('timestamp', 'desc')
    )
    
    if (limit) {
      // Note: We'll need to import `limit` from firestore
      // q = query(q, limit(limit))
    }
    
    return onSnapshot(q, (snapshot) => {
      const events = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as BookEvent))
      
      callback(events)
    })
  },
  
  // Add new event
  async addEvent(
    userId: string, 
    eventData: Omit<BookEvent, 'id' | 'userId' | 'timestamp'>
  ): Promise<string> {
    const docRef = await addDoc(collection(firestore, `users/${userId}/events`), {
      ...eventData,
      userId,
      timestamp: serverTimestamp()
    })
    
    return docRef.id
  },

  // Helper methods for specific event types
  async logStateChange(
    userId: string,
    bookId: string,
    from: string,
    to: string
  ): Promise<string> {
    return this.addEvent(userId, {
      bookId,
      type: 'state_change',
      data: {
        stateChange: { from: from as any, to: to as any }
      }
    })
  },

  async logProgressUpdate(
    userId: string,
    bookId: string,
    from: number,
    to: number
  ): Promise<string> {
    const pagesRead = to - from
    return this.addEvent(userId, {
      bookId,
      type: 'progress_update',
      data: {
        progressUpdate: { from, to, pagesRead }
      }
    })
  },

  async logComment(
    userId: string,
    bookId: string,
    text: string,
    page?: number
  ): Promise<string> {
    return this.addEvent(userId, {
      bookId,
      type: 'comment',
      data: {
        comment: { text, page }
      }
    })
  },

  async logQuote(
    userId: string,
    bookId: string,
    text: string,
    page?: number,
    chapter?: string
  ): Promise<string> {
    return this.addEvent(userId, {
      bookId,
      type: 'quote',
      data: {
        quote: { text, page, chapter }
      }
    })
  },

  async logReview(
    userId: string,
    bookId: string,
    rating: number,
    text: string,
    spoilerFree: boolean = true
  ): Promise<string> {
    return this.addEvent(userId, {
      bookId,
      type: 'review',
      data: {
        review: { rating, text, spoilerFree }
      }
    })
  }
}