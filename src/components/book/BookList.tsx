import type { Book } from '@/types'
import { BookCard } from './BookCard'

interface BookListProps {
  books: Book[]
  title?: string
}

export const BookList = ({ books, title }: BookListProps) => {
  if (books.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="text-6xl mb-4">📚</div>
        <h3 className="text-lg font-medium text-gray-900 mb-2">No books found</h3>
        <p className="text-gray-500">Start building your library by adding some books!</p>
      </div>
    )
  }

  return (
    <div>
      {title && (
        <h2 className="text-2xl font-bold text-gray-900 mb-6">{title}</h2>
      )}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {books.map((book) => (
          <BookCard key={book.id} book={book} />
        ))}
      </div>
    </div>
  )
}