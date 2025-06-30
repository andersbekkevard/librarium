'use client'

import React from 'react'
import { Book, BookState } from '@/types'
import { Card, CardContent, CardFooter } from '@/components/ui/Card'
import { Badge } from '@/components/ui/Badge'
import { Button } from '@/components/ui/Button'
import { Progress } from '@/components/ui/Progress'
import { BookOpen, Star, Clock, CheckCircle2 } from 'lucide-react'

interface BookCardProps {
  book: Book
  onStateChange?: (newState: BookState) => void
  onProgressUpdate?: (progress: number) => void
}

const BookCard = ({ book, onStateChange, onProgressUpdate }: BookCardProps) => {
  const getStateIcon = (state: BookState) => {
    switch (state) {
      case 'not_started':
        return <Clock className="h-4 w-4" />
      case 'in_progress':
        return <BookOpen className="h-4 w-4" />
      case 'finished':
        return <CheckCircle2 className="h-4 w-4" />
    }
  }

  const getStateColor = (state: BookState) => {
    switch (state) {
      case 'not_started':
        return 'secondary'
      case 'in_progress':
        return 'default'
      case 'finished':
        return 'destructive'
    }
  }

  const getNextState = (currentState: BookState): BookState | null => {
    switch (currentState) {
      case 'not_started':
        return 'in_progress'
      case 'in_progress':
        return 'finished'
      case 'finished':
        return 'in_progress'
      default:
        return null
    }
  }

  const getStateActionText = (currentState: BookState): string => {
    switch (currentState) {
      case 'not_started':
        return 'Start Reading'
      case 'in_progress':
        return 'Mark Finished'
      case 'finished':
        return 'Re-read'
    }
  }

  const handleStateAction = () => {
    const nextState = getNextState(book.state)
    if (nextState && onStateChange) {
      onStateChange(nextState)
    }
  }

  const progressPercentage = book.metadata.pages && book.progress 
    ? (book.progress / book.metadata.pages) * 100 
    : 0

  return (
    <Card className="w-full max-w-sm">
      <CardContent className="p-4">
        <BookCard.Cover src={book.metadata.coverUrl} alt={book.title} />
        <BookCard.Content>
          <BookCard.Title>{book.title}</BookCard.Title>
          <BookCard.Author>{book.author}</BookCard.Author>
          <BookCard.State state={book.state} />
          
          {book.state === 'in_progress' && book.metadata.pages && (
            <BookCard.Progress 
              current={book.progress || 0} 
              total={book.metadata.pages}
              percentage={progressPercentage}
            />
          )}
          
          {book.metadata.rating && (
            <div className="flex items-center gap-1 mt-2">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="text-sm text-muted-foreground">
                {book.metadata.rating}/5
              </span>
            </div>
          )}
        </BookCard.Content>
      </CardContent>
      
      <CardFooter className="p-4 pt-0">
        <BookCard.Actions>
          <Button 
            size="sm" 
            onClick={handleStateAction}
            className="w-full"
          >
            {getStateActionText(book.state)}
          </Button>
        </BookCard.Actions>
      </CardFooter>
    </Card>
  )
}

BookCard.Cover = ({ src, alt }: { src?: string; alt: string }) => (
  <div className="w-full h-48 bg-muted rounded-md mb-3 overflow-hidden">
    {src ? (
      <img 
        src={src} 
        alt={alt} 
        className="w-full h-full object-cover"
      />
    ) : (
      <div className="w-full h-full flex items-center justify-center text-muted-foreground">
        <BookOpen className="h-12 w-12" />
      </div>
    )}
  </div>
)

BookCard.Content = ({ children }: { children: React.ReactNode }) => (
  <div className="space-y-2">
    {children}
  </div>
)

BookCard.Title = ({ children }: { children: string }) => (
  <h3 className="font-semibold text-lg leading-tight line-clamp-2">
    {children}
  </h3>
)

BookCard.Author = ({ children }: { children: string }) => (
  <p className="text-muted-foreground text-sm">
    by {children}
  </p>
)

BookCard.State = ({ state }: { state: BookState }) => {
  const getStateIcon = (state: BookState) => {
    switch (state) {
      case 'not_started':
        return <Clock className="h-3 w-3" />
      case 'in_progress':
        return <BookOpen className="h-3 w-3" />
      case 'finished':
        return <CheckCircle2 className="h-3 w-3" />
    }
  }

  const getStateColor = (state: BookState) => {
    switch (state) {
      case 'not_started':
        return 'secondary' as const
      case 'in_progress':
        return 'default' as const
      case 'finished':
        return 'outline' as const
    }
  }

  const getStateText = (state: BookState) => {
    switch (state) {
      case 'not_started':
        return 'Not Started'
      case 'in_progress':
        return 'Reading'
      case 'finished':
        return 'Finished'
    }
  }

  return (
    <Badge variant={getStateColor(state)} className="w-fit">
      {getStateIcon(state)}
      <span className="ml-1">{getStateText(state)}</span>
    </Badge>
  )
}

BookCard.Progress = ({ 
  current, 
  total, 
  percentage 
}: { 
  current: number
  total: number
  percentage: number
}) => (
  <div className="space-y-1">
    <div className="flex justify-between text-xs text-muted-foreground">
      <span>Progress</span>
      <span>{current}/{total} pages</span>
    </div>
    <Progress value={percentage} className="h-2" />
    <div className="text-right text-xs text-muted-foreground">
      {Math.round(percentage)}%
    </div>
  </div>
)

BookCard.Actions = ({ children }: { children: React.ReactNode }) => (
  <div className="flex gap-2 w-full">
    {children}
  </div>
)

export { BookCard }