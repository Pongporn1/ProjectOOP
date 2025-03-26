import React, { ReactNode } from 'react';
import {UniqueIdentifier, useDroppable} from '@dnd-kit/core';

export function Droppable(props: {className: string, id: UniqueIdentifier, children: ReactNode}) {
  const {isOver, setNodeRef} = useDroppable({
    id: props.id,
  });
  const style = {
    opacity: isOver ? 0 : 1,
    
  };

  return (
    <div className={props.className} ref={setNodeRef} style={style}>
      {props.children}
    </div>
  );
}
  