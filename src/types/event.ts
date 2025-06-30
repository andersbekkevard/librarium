import { BookState } from './book'

export type BookEventType = 'state_change' | 'progress_update' | 'comment' | 'quote' | 'review'

export interface StateChangeData {
  from: BookState
  to: BookState
}

export interface ProgressUpdateData {
  from: number
  to: number
  pagesRead?: number
}

export interface CommentData {
  text: string
  page?: number
}

export interface QuoteData {
  text: string
  page?: number
  chapter?: string
}

export interface ReviewData {
  rating: number
  text: string
  spoilerFree: boolean
}

export type BookEventData = 
  | { type: 'state_change'; data: { stateChange: StateChangeData } }
  | { type: 'progress_update'; data: { progressUpdate: ProgressUpdateData } }
  | { type: 'comment'; data: { comment: CommentData } }
  | { type: 'quote'; data: { quote: QuoteData } }
  | { type: 'review'; data: { review: ReviewData } }

export interface BookEvent {
  id: string
  bookId: string
  userId: string
  type: BookEventType
  timestamp: Date
  data: {
    stateChange?: StateChangeData
    progressUpdate?: ProgressUpdateData
    comment?: CommentData
    quote?: QuoteData
    review?: ReviewData
  }
}

export interface ValidationResult {
  valid: boolean
  errors?: string[]
  error?: string
}