```mermaid
sequenceDiagram
    participant User
    participant OwnedBook
    participant ReadingState
    participant BookHistory
    participant BookEvent

    User->>OwnedBook: addComment("Great book!")
    OwnedBook->>ReadingState: handleComment("Great book!")
    
    alt State is NotStartedState
        ReadingState-->>OwnedBook: throw UnsupportedOperationException
        OwnedBook-->>User: Error: Cannot comment in NotStartedState
    else State is InProgressState
        ReadingState->>BookEvent: new BookEvent(COMMENT, "Great book!", pageNumber)
        ReadingState->>BookHistory: addEvent(commentEvent)
        BookHistory-->>ReadingState: Success
        ReadingState-->>OwnedBook: Success
        OwnedBook-->>User: Comment added
    else State is FinishedState
        ReadingState->>BookEvent: new BookEvent(AFTERTHOUGHT, "Great book!", pageNumber)
        ReadingState->>BookHistory: addEvent(afterThoughtEvent)
        BookHistory-->>ReadingState: Success
        ReadingState-->>OwnedBook: Success
        OwnedBook-->>User: Afterthought added
    end

    Note over User, BookEvent: State Transition Example
    
    User->>OwnedBook: changeState()
    OwnedBook->>ReadingState: changeState()
    
    alt State is NotStartedState
        ReadingState->>OwnedBook: setState(new InProgressState(book))
        ReadingState->>BookEvent: new BookEvent(STARTED_READING)
        ReadingState->>BookHistory: addEvent(startedReadingEvent)
        OwnedBook-->>User: State changed to InProgress
    else State is InProgressState
        ReadingState->>OwnedBook: setState(new FinishedState(book))
        ReadingState->>BookEvent: new BookEvent(FINISHED_READING)
        ReadingState->>BookHistory: addEvent(finishedReadingEvent)
        OwnedBook-->>User: State changed to Finished
    else State is FinishedState
        ReadingState-->>OwnedBook: No change (already finished)
        OwnedBook-->>User: No change (already finished)
    end
