
import React, { ReactNode } from 'react';
import {useDraggable} from '@dnd-kit/core';
import {CSS} from '@dnd-kit/utilities';

export function Draggable(props: {id: string, disabled: boolean, children: ReactNode, minion:number}) {
  const {attributes, listeners, setNodeRef, transform, isDragging} = useDraggable({
    id: props.id,
    disabled: props.disabled,
    data: {"minion":props.minion}
  });
  const style = {
    // Outputs `translate3d(x, y, 0)`
    transform: CSS.Translate.toString(transform),
    
  };

  return (
    <button className={`${ isDragging ?  "w-[80%] h-[80%]":"w-full h-full"} flex items-center justify-center origin-center`} ref={setNodeRef} style={style} {...listeners} {...attributes}>
      {props.children}
    </button>
  );
}
