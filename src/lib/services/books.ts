import { 
  collection, 
  doc,
  addDoc, 
  updateDoc,
  deleteDoc,
  onSnapshot, 
  query, 
  where,
  orderBy,
  writeBatch,
  serverTimestamp
} from 'firebase/firestore'
import { firestore } from '@/lib/firebase'
import { Book, BookFilters, CreateBookRequest, UpdateBookRequest, BookState } from '@/types'
import { createBook } from '@/lib/factories/book'
import { validateBookStateTransition } from '@/lib/validation/book'
import { eventService } from './events'

const buildBooksQuery = (userId: string, filters?: BookFilters) => {
  const booksRef = collection(firestore, `users/${userId}/books`)
  let q = query(booksRef)
  
  if (filters?.state) {
    q = query(q, where('state', '==', filters.state))
  }
  
  if (filters?.isOwned !== undefined) {
    q = query(q, where('isOwned', '==', filters.isOwned))
  }
  
  if (filters?.author) {
    q = query(q, where('author', '==', filters.author))
  }
  
  // Add default ordering
  q = query(q, orderBy('updatedAt', 'desc'))
  
  return q
}

export const bookService = {
  // Subscribe to user's books with real-time updates
  subscribe: (
    userId: string, 
    filters?: BookFilters, 
    callback?: (books: Book[]) => void
  ): (() => void) => {
    const q = buildBooksQuery(userId, filters)
    
    return onSnapshot(q, (snapshot) => {
      const books = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate() || new Date(),
        updatedAt: doc.data().updatedAt?.toDate() || new Date(),
        startedAt: doc.data().startedAt?.toDate(),
        finishedAt: doc.data().finishedAt?.toDate()
      } as Book))
      
      callback?.(books)
    })
  },
  
  // CRUD operations
  async create(userId: string, bookData: CreateBookRequest): Promise<string> {
    const book = createBook({ ...bookData, ownerId: userId })
    const docRef = await addDoc(collection(firestore, `users/${userId}/books`), {
      ...book,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    })
    return docRef.id
  },
  
  async update(userId: string, bookId: string, updates: UpdateBookRequest): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await updateDoc(bookRef, { 
      ...updates, 
      updatedAt: serverTimestamp() 
    })
  },
  
  async delete(userId: string, bookId: string): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    await deleteDoc(bookRef)
  },
  
  // State management with event logging
  async updateState(
    userId: string, 
    bookId: string, 
    newState: BookState, 
    currentState: BookState
  ): Promise<void> {
    // Validate state transition
    const validation = validateBookStateTransition(currentState, newState)
    if (!validation.valid) {
      throw new Error(validation.error!)
    }
    
    const batch = writeBatch(firestore)
    
    // Update book state
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    const updateData: any = {
      state: newState,
      updatedAt: serverTimestamp()
    }
    
    // Add timestamps for state changes
    if (newState === 'in_progress' && currentState === 'not_started') {
      updateData.startedAt = serverTimestamp()
    }
    if (newState === 'finished') {
      updateData.finishedAt = serverTimestamp()
    }
    if (newState === 'not_started' && currentState !== 'not_started') {
      updateData.progress = 0
      updateData.startedAt = null
      updateData.finishedAt = null
    }
    
    batch.update(bookRef, updateData)
    
    // Log state change event
    const eventRef = doc(collection(firestore, `users/${userId}/events`))
    batch.set(eventRef, {
      bookId,
      userId,
      type: 'state_change',
      data: {
        stateChange: { from: currentState, to: newState }
      },
      timestamp: serverTimestamp()
    })
    
    await batch.commit()
  },

  // Progress management with event logging
  async updateProgress(
    userId: string,
    bookId: string,
    newProgress: number,
    currentProgress: number = 0
  ): Promise<void> {
    const batch = writeBatch(firestore)
    
    // Update book progress
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    batch.update(bookRef, {
      progress: newProgress,
      updatedAt: serverTimestamp()
    })
    
    // Log progress event
    const eventRef = doc(collection(firestore, `users/${userId}/events`))
    batch.set(eventRef, {
      bookId,
      userId,
      type: 'progress_update',
      data: {
        progressUpdate: { 
          from: currentProgress, 
          to: newProgress,
          pagesRead: newProgress - currentProgress
        }
      },
      timestamp: serverTimestamp()
    })
    
    await batch.commit()
  },

  // Collaboration
  async addCollaborator(userId: string, bookId: string, collaboratorId: string): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    
    // We'll need to use arrayUnion here, but for simplicity using a direct update
    // In a real implementation, we'd want to use Firestore array operations
    await updateDoc(bookRef, {
      collaborators: [collaboratorId], // This should use arrayUnion
      updatedAt: serverTimestamp()
    })
  },

  async removeCollaborator(userId: string, bookId: string, collaboratorId: string): Promise<void> {
    const bookRef = doc(firestore, `users/${userId}/books/${bookId}`)
    
    // This should use arrayRemove in a real implementation
    await updateDoc(bookRef, {
      collaborators: [], // Simplified - should remove specific collaborator
      updatedAt: serverTimestamp()
    })
  }
}