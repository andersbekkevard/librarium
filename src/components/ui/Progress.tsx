import * as React from "react"

import { cn } from "@/lib/utils"

const Progress = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement> & {
    value?: number
    max?: number
    showLabel?: boolean
  }
>(({ className, value = 0, max = 100, showLabel = false, ...props }, ref) => {
  const percentage = Math.round((value / max) * 100)
  
  return (
    <div ref={ref} className={cn("w-full", className)} {...props}>
      {showLabel && (
        <div className="flex items-center justify-between mb-1">
          <span className="text-sm font-medium text-muted-foreground">
            {value} / {max} pages
          </span>
          <span className="text-sm font-medium text-muted-foreground">
            {percentage}%
          </span>
        </div>
      )}
      <div className="relative h-2 w-full overflow-hidden rounded-full bg-secondary">
        <div
          className="h-full bg-primary transition-all duration-300 ease-out"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  )
})
Progress.displayName = "Progress"

export { Progress }