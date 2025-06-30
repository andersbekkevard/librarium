export interface ShelfCollaborator {
  userId: string
  permission: 'read' | 'write' | 'admin'
  addedAt: Date
}

export interface Shelf {
  id: string
  name: string
  description?: string
  bookIds: string[]
  
  // Ownership & Sharing
  ownerId: string
  collaborators: ShelfCollaborator[]
  
  // Visibility
  isPublic: boolean
  isDefault: boolean
  
  // Organization
  sortOrder: 'manual' | 'title' | 'author' | 'dateAdded' | 'dateRead'
  color?: string
  icon?: string
  
  createdAt: Date
  updatedAt: Date
}

export interface SharedShelfMetadata {
  id: string
  name: string
  description?: string
  ownerId: string
  collaborators: ShelfCollaborator[]
  isPublic: boolean
  createdAt: Date
  updatedAt: Date
}

export interface SharedBook extends Omit<import('./book').Book, 'id'> {
  originalBookId: string
  shelfId: string
}

export interface ShelfEvent {
  id: string
  shelfId: string
  userId: string
  type: 'book_added' | 'book_removed' | 'collaborator_added' | 'collaborator_removed' | 'settings_changed'
  timestamp: Date
  data: {
    bookId?: string
    collaboratorId?: string
    changes?: Record<string, any>
  }
}