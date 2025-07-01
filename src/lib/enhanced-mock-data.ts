import type { Book } from '@/types'
import type { 
  BookEvent, 
  ReadingGoal, 
  UserStatistics, 
  Shelf, 
  Household, 
  Collaboration,
  DashboardData
} from '@/types/enhanced'

// Enhanced mock books with more detailed data
export const enhancedMockBooks: Book[] = [
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
      genre: ['Programming', 'Software Engineering', 'Technology'],
      publishedYear: 2019,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71f743sOPoL.jpg',
      description: 'The classic guide to better programming and software development. Updated for the modern developer.',
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
    collaborators: ['user-2'],
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
      genre: ['Science Fiction', 'Fantasy', 'Space Opera'],
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
    state: 'finished',
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 288,
      genre: ['Fiction', 'Philosophy', 'Fantasy'],
      publishedYear: 2020,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71BknLWdzuL.jpg',
      description: 'A novel about all the choices that go into a life well lived.',
      rating: 4
    },
    createdAt: new Date('2024-06-10'),
    updatedAt: new Date('2024-06-25'),
    startedAt: new Date('2024-06-10'),
    finishedAt: new Date('2024-06-25')
  },
  {
    id: '7',
    title: 'Project Hail Mary',
    author: 'Andy Weir',
    isbn: '9780593135204',
    state: 'finished',
    isOwned: true,
    ownerId: 'demo-user',
    metadata: {
      pages: 496,
      genre: ['Science Fiction', 'Space Opera', 'Adventure'],
      publishedYear: 2021,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71QaYQBj7VL.jpg',
      description: 'A lone astronaut must save humanity in this gripping sci-fi thriller.',
      rating: 5
    },
    createdAt: new Date('2024-04-01'),
    updatedAt: new Date('2024-04-20'),
    startedAt: new Date('2024-04-01'),
    finishedAt: new Date('2024-04-20')
  },
  {
    id: '8',
    title: 'Klara and the Sun',
    author: 'Kazuo Ishiguro',
    isbn: '9780571364909',
    state: 'not_started',
    isOwned: false,
    ownerId: 'demo-user',
    metadata: {
      pages: 304,
      genre: ['Fiction', 'Literary Fiction', 'Science Fiction'],
      publishedYear: 2021,
      coverUrl: 'https://images-na.ssl-images-amazon.com/images/I/71QqnzjVNbL.jpg',
      description: 'A thrilling book about artificial intelligence and what it means to love.'
    },
    createdAt: new Date('2024-06-15'),
    updatedAt: new Date('2024-06-15')
  }
]

// Mock book events
export const mockBookEvents: BookEvent[] = [
  {
    id: 'event-1',
    bookId: '6',
    userId: 'demo-user',
    type: 'state_change',
    timestamp: new Date('2024-06-25T14:30:00'),
    data: {
      stateChange: { from: 'in_progress', to: 'finished' }
    }
  },
  {
    id: 'event-2',
    bookId: '6',
    userId: 'demo-user',
    type: 'review',
    timestamp: new Date('2024-06-25T14:35:00'),
    data: {
      review: { text: 'A beautiful and philosophical exploration of alternate lives. Really made me think about choices and regret.', spoilerFree: true }
    }
  },
  {
    id: 'event-3',
    bookId: '2',
    userId: 'demo-user',
    type: 'progress_update',
    timestamp: new Date('2024-06-15T20:15:00'),
    data: {
      progressUpdate: { from: 150, to: 180, pagesRead: 30 }
    }
  },
  {
    id: 'event-4',
    bookId: '5',
    userId: 'demo-user',
    type: 'quote',
    timestamp: new Date('2024-06-10T19:45:00'),
    data: {
      quote: { text: 'You do not rise to the level of your goals. You fall to the level of your systems.', page: 27, chapter: '1' }
    }
  },
  {
    id: 'event-5',
    bookId: '7',
    userId: 'demo-user',
    type: 'rating',
    timestamp: new Date('2024-04-20T16:20:00'),
    data: {
      rating: { rating: 5 }
    }
  }
]

// Mock reading goals
export const mockReadingGoals: ReadingGoal[] = [
  {
    id: 'goal-1',
    userId: 'demo-user',
    title: '2024 Reading Challenge',
    description: 'Read 24 books in 2024',
    targetType: 'books',
    targetValue: 24,
    currentValue: 15,
    startDate: new Date('2024-01-01'),
    endDate: new Date('2024-12-31'),
    status: 'active',
    createdAt: new Date('2023-12-31')
  },
  {
    id: 'goal-2',
    userId: 'demo-user',
    title: 'Summer Reading Sprint',
    description: 'Read 5000 pages during summer',
    targetType: 'pages',
    targetValue: 5000,
    currentValue: 3200,
    startDate: new Date('2024-06-01'),
    endDate: new Date('2024-08-31'),
    status: 'active',
    createdAt: new Date('2024-05-30')
  },
  {
    id: 'goal-3',
    userId: 'demo-user',
    title: 'Sci-Fi Deep Dive',
    description: 'Read 6 science fiction novels',
    targetType: 'books',
    targetValue: 6,
    currentValue: 2,
    startDate: new Date('2024-04-01'),
    endDate: new Date('2024-09-30'),
    status: 'active',
    createdAt: new Date('2024-03-28')
  }
]

// Mock user statistics
export const mockUserStatistics: UserStatistics = {
  userId: 'demo-user',
  totalBooks: {
    owned: 6,
    wishlist: 2,
    finished: 4,
    inProgress: 2,
    notStarted: 2
  },
  totalPages: 1952,
  averageRating: 4.5,
  readingStreaks: {
    current: 7,
    longest: 23,
    lastActivityDate: new Date('2024-06-25')
  },
  yearlyStats: {
    '2024': {
      booksRead: 4,
      pagesRead: 1952,
      averageRating: 4.5,
      favoriteGenres: ['Programming', 'Science Fiction', 'Psychology']
    },
    '2023': {
      booksRead: 18,
      pagesRead: 5640,
      averageRating: 4.2,
      favoriteGenres: ['Fiction', 'Programming', 'Design']
    }
  },
  genreBreakdown: {
    'Programming': 2,
    'Science Fiction': 2,
    'Psychology': 2,
    'Design': 1,
    'Fiction': 1,
    'Philosophy': 1,
    'Self-Help': 1
  },
  readingSpeed: {
    averagePagesPerDay: 25,
    averageDaysPerBook: 15
  },
  monthlyProgress: {
    '2024-01': { booksCompleted: 1, pagesRead: 352 },
    '2024-02': { booksCompleted: 0, pagesRead: 180 },
    '2024-03': { booksCompleted: 0, pagesRead: 150 },
    '2024-04': { booksCompleted: 2, pagesRead: 864 },
    '2024-05': { booksCompleted: 0, pagesRead: 120 },
    '2024-06': { booksCompleted: 1, pagesRead: 286 }
  }
}

// Mock shelves
export const mockShelves: Shelf[] = [
  {
    id: 'shelf-1',
    name: 'Currently Reading',
    description: 'Books I am actively reading',
    bookIds: ['2', '5'],
    ownerId: 'demo-user',
    collaborators: [],
    isPublic: false,
    isDefault: true,
    color: '#3b82f6',
    icon: 'book-open',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-06-15')
  },
  {
    id: 'shelf-2',
    name: 'Programming Library',
    description: 'Technical books about programming and software development',
    bookIds: ['1', '2'],
    ownerId: 'demo-user',
    collaborators: [],
    isPublic: true,
    isDefault: false,
    color: '#10b981',
    icon: 'code',
    createdAt: new Date('2024-01-15'),
    updatedAt: new Date('2024-02-01')
  },
  {
    id: 'shelf-3',
    name: 'Fiction Favorites',
    description: 'My favorite fiction books',
    bookIds: ['3', '6', '7', '8'],
    ownerId: 'demo-user',
    collaborators: [],
    isPublic: false,
    isDefault: false,
    color: '#8b5cf6',
    icon: 'heart',
    createdAt: new Date('2024-03-01'),
    updatedAt: new Date('2024-06-25')
  }
]

// Mock household
export const mockHousehold: Household = {
  id: 'household-1',
  name: 'The Smith Family',
  description: 'Our family book collection',
  members: [
    {
      userId: 'demo-user',
      name: 'John Doe',
      email: 'john@example.com',
      role: 'owner',
      joinedAt: new Date('2024-01-01')
    },
    {
      userId: 'user-2',
      name: 'Jane Doe',
      email: 'jane@example.com',
      role: 'admin',
      joinedAt: new Date('2024-01-02')
    },
    {
      userId: 'user-3',
      name: 'Alex Doe',
      email: 'alex@example.com',
      role: 'member',
      joinedAt: new Date('2024-02-15')
    }
  ],
  sharedBooks: ['1', '2', '4', '7'],
  location: 'Living Room Bookshelf',
  settings: {
    allowBookLending: true,
    requireApprovalForNewMembers: false,
    defaultBookVisibility: 'household'
  },
  createdAt: new Date('2024-01-01')
}

// Mock collaborations
export const mockCollaborations: Collaboration[] = [
  {
    id: 'collab-1',
    bookId: '2',
    ownerId: 'demo-user',
    collaboratorId: 'user-2',
    permission: 'write',
    status: 'accepted',
    invitedAt: new Date('2024-02-05'),
    respondedAt: new Date('2024-02-06')
  }
]

// Dashboard data
export const mockDashboardData: DashboardData = {
  recentActivity: mockBookEvents.slice(0, 5),
  currentlyReading: enhancedMockBooks.filter(book => book.state === 'in_progress'),
  recentlyFinished: enhancedMockBooks.filter(book => book.state === 'finished').slice(-2),
  upcomingGoals: mockReadingGoals.filter(goal => goal.status === 'active').slice(0, 3),
  quickStats: {
    booksThisMonth: 1,
    pagesThisWeek: 45,
    currentStreak: 7,
    completionRate: 75
  }
}