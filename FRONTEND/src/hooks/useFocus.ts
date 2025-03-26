import { useRef, useEffect, RefObject } from 'react'

export default function useFocus<T extends HTMLElement>(): RefObject<T | null> {
  const inputRef = useRef<T | null>(null)

/*   useEffect(() => {
    inputRef.current?.focus()
  }, []) */

  return inputRef
}
