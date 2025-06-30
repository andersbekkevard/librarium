import { Book, CreateBookParams, BookEvent, BookEventData } from '@/types'

export const createBook = (params: CreateBookParams): Omit<Book, 'id'> => {
  const now = new Date()
  
  return {
    title: params.title.trim(),
    author: params.author.trim(),
    state: 'not_started',
    progress: 0,
    isOwned: params.isOwned ?? false,
    ownerId: params.ownerId,
    collaborators: params.collaborators || [],
    metadata: {
      pages: params.metadata?.pages,
      genre: params.metadata?.genre || [],
      publishedYear: params.metadata?.publishedYear,
      coverUrl: params.metadata?.coverUrl,
      description: params.metadata?.description,
      isbn: params.metadata?.isbn
    },
    createdAt: now,
    updatedAt: now
  }
}

export const createBookEvent = (
  bookId: string,
  userId: string,
  eventData: BookEventData
): Omit<BookEvent, 'id'> => ({
  bookId,
  userId,
  timestamp: new Date(),
  type: eventData.type,
  data: eventData.data
})

export const createDefaultShelves = (userId: string) => [
  {
    name: 'Currently Reading',
    description: 'Books you are currently reading',
    ownerId: userId,
    isDefault: true,
    isPublic: false,
    sortOrder: 'manual' as const,
    bookIds: [],
    collaborators: [],
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    name: 'Finished',
    description: 'Books you have completed',
    ownerId: userId,
    isDefault: true,
    isPublic: false,
    sortOrder: 'dateRead' as const,
    bookIds: [],
    collaborators: [],
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    name: 'Wishlist',
    description: 'Books you want to read',
    ownerId: userId,
    isDefault: true,
    isPublic: false,
    sortOrder: 'dateAdded' as const,
    bookIds: [],
    collaborators: [],
    createdAt: new Date(),
    updatedAt: new Date()
  }
]