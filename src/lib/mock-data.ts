import type { Book } from '@/types'

export const mockBooks: Book[] = [
  {
    id: '1',
    title: 'The Pragmatic Programmer',
    author: 'David Thomas, Andrew Hunt',
    isbn: '9780135957059',
    state: 'finished',
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 352,
      genre: ['Programming', 'Software Engineering'],
      publishedYear: 2019,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71f743sOPoL.jpg',
      description: 'The classic guide to better programming and software development.',
      rating: 5
    },
    createdAt: new Date('2024-01-15'),
    updatedAt: new Date('2024-02-01'),
    startedAt: new Date('2024-01-15'),
    finishedAt: new Date('2024-02-01')
  },
  {
    id: '2',
    title: 'Clean Code',
    author: 'Robert C. Martin',
    isbn: '9780132350884',
    state: 'in_progress',
    progress: 180,
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 464,
      genre: ['Programming', 'Software Engineering'],
      publishedYear: 2008,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/515iEcDr1GL.jpg',
      description: 'A handbook of agile software craftsmanship.',
      rating: 4
    },
    createdAt: new Date('2024-02-05'),
    updatedAt: new Date('2024-06-15'),
    startedAt: new Date('2024-02-10')
  },
  {
    id: '3',
    title: 'Dune',
    author: 'Frank Herbert',
    isbn: '9780441172719',
    state: 'not_started',
    isOwned: false,
    ownerId: 'demo-user',
    metadata: {
      pages: 688,
      genre: ['Science Fiction', 'Fantasy'],
      publishedYear: 1965,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/81ym1Ln5+uL.jpg',
      description: 'Epic science fiction novel set on the desert planet Arrakis.'
    },
    createdAt: new Date('2024-06-01'),
    updatedAt: new Date('2024-06-01')
  },
  {
    id: '4',
    title: 'The Design of Everyday Things',
    author: 'Don Norman',
    isbn: '9780465050659',
    state: 'finished',
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 368,
      genre: ['Design', 'Psychology', 'UX'],
      publishedYear: 2013,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/416Hql52NCL.jpg',
      description: 'Essential reading for anyone who designs anything used by humans.',
      rating: 5
    },
    createdAt: new Date('2024-03-01'),
    updatedAt: new Date('2024-04-15'),
    startedAt: new Date('2024-03-05'),
    finishedAt: new Date('2024-04-15')
  },
  {
    id: '5',
    title: 'Atomic Habits',
    author: 'James Clear',
    isbn: '9780735211292',
    state: 'in_progress',
    progress: 120,
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 320,
      genre: ['Self-Help', 'Psychology', 'Productivity'],
      publishedYear: 2018,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/51Tlm0GZTXL.jpg',
      description: 'An easy & proven way to build good habits & break bad ones.',
      rating: 4
    },
    createdAt: new Date('2024-05-10'),
    updatedAt: new Date('2024-06-20'),
    startedAt: new Date('2024-05-15')
  },
  {
    id: '6',
    title: 'The Midnight Library',
    author: 'Matt Haig',
    isbn: '9780525559474',
    state: 'not_started',
    isOwned: false,
    ownerId: 'demo-user',
    metadata: {
      pages: 288,
      genre: ['Fiction', 'Philosophy', 'Fantasy'],
      publishedYear: 2020,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71BknLWdzuL.jpg',
      description: 'A novel about all the choices that go into a life well lived.'
    },
    createdAt: new Date('2024-06-10'),
    updatedAt: new Date('2024-06-10')
  }
]