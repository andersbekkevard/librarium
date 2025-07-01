import Image from 'next/image'
import type { Book } from '@/types'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Card, CardContent } from '@/components/ui/card'

interface BookCardProps {
  book: Book
}

const stateLabels = {
  not_started: 'Not Started',
  in_progress: 'Reading',
  finished: 'Finished'
}

const stateVariants = {
  not_started: 'secondary' as const,
  in_progress: 'warning' as const,
  finished: 'success' as const
}

export const BookCard = ({ book }: BookCardProps) => {
  const handleCardClick = () => {
    // Future: Navigate to book detail page
    console.log('Book clicked:', book.title)
  }

  const handleCardKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      handleCardClick()
    }
  }

  return (
    <Card 
      className="overflow-hidden hover:shadow-lg transition-shadow duration-200 cursor-pointer group"
      onClick={handleCardClick}
      onKeyDown={handleCardKeyDown}
      tabIndex={0}
      role="button"
      aria-label={`View details for ${book.title} by ${book.author}`}
    >
      <div className="relative h-48 bg-muted">
        {book.metadata.coverUrl ? (
          <Image
            src={book.metadata.coverUrl}
            alt={`Cover of ${book.title}`}
            fill
            className="object-cover group-hover:scale-105 transition-transform duration-200"
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          />
        ) : (
          <div className="flex items-center justify-center h-full">
            <div className="text-center text-muted-foreground">
              <div className="text-2xl mb-2">📚</div>
              <p className="text-sm font-medium">{book.title}</p>
            </div>
          </div>
        )}
        <div className="absolute top-2 right-2">
          <Badge variant={stateVariants[book.state]}>
            {stateLabels[book.state]}
          </Badge>
        </div>
        {!book.isOwned && (
          <div className="absolute top-2 left-2">
            <Badge variant="secondary">Wishlist</Badge>
          </div>
        )}
      </div>
      
      <CardContent className="p-4">
        <h3 className="font-semibold text-lg text-card-foreground mb-1 truncate">
          {book.title}
        </h3>
        <p className="text-muted-foreground text-sm mb-2">{book.author}</p>
        
        {book.metadata.genre && book.metadata.genre.length > 0 && (
          <div className="flex flex-wrap gap-1 mb-3">
            {book.metadata.genre.slice(0, 2).map((genre) => (
              <Badge key={genre} variant="outline">
                {genre}
              </Badge>
            ))}
            {book.metadata.genre.length > 2 && (
              <Badge variant="outline">
                +{book.metadata.genre.length - 2}
              </Badge>
            )}
          </div>
        )}
        
        {book.state === 'in_progress' && book.progress && book.metadata.pages && (
          <div className="mb-3">
            <Progress 
              value={book.progress} 
              max={book.metadata.pages} 
              showLabel 
            />
          </div>
        )}
        
        <div className="flex items-center justify-between text-sm text-muted-foreground">
          <span>
            {book.metadata.pages ? `${book.metadata.pages} pages` : 'Unknown length'}
          </span>
          {book.metadata.rating && book.state === 'finished' && (
            <div className="flex items-center">
              <span className="mr-1">⭐</span>
              <span>{book.metadata.rating}/5</span>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}