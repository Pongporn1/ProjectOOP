import React, { useEffect, useRef, useState } from 'react';

type TextAreaProps = React.ComponentProps<'textarea'>;
const ControlledTextArea: React.FC<TextAreaProps> = (props) => {
   const { value, onChange, ...rest } = props;
   const [cursor, setCursor] = useState<number | null>(null);
   const ref = useRef<HTMLTextAreaElement>(null);

   useEffect(() => {
      ref.current?.setSelectionRange(cursor, cursor);
   }, [ref, cursor, value]);

   const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      setCursor(e.target.selectionStart);
      onChange?.(e);
   };

   return <textarea ref={ref} value={value} onChange={handleChange} {...rest} />;
};

export default ControlledTextArea;