import { cn } from '@/lib/utils/cs';
import { IoSend } from 'react-icons/io5';
import {
  Button,
  type ButtonProps as BaseButtonProps,
} from './button';

type ButtonProps = {
  children: React.ReactNode;
} & BaseButtonProps;

//======================================
export const Button_v5 = ({
  children,
  Icon = <IoSend size="20" />,
  hoverClass = "bg-zinc-100",
  ...rest
}: ButtonProps & { Icon: React.ReactNode } & {hoverClass?: string}) => {
  return (
    <Button
      {...rest}
      className={cn(
        'relative overflow-hidden border shadow group',
        // light mode
        'border-zinc-300 text-zinc-800 bg-zinc-50',
        // dark mode
        'dark:border-zinc-700 dark:text-zinc-100 dark:bg-zinc-950',
        rest.className,
      )}
    >
      <span className={`absolute inset-0 rounded-sm flex items-center justify-center size-full duration-700 ease-[cubic-bezier(0.50,0.20,0,1)] -translate-x-full group-hover:translate-x-0 ${hoverClass} text-zinc-100 dark:text-zinc-800`}>
        {Icon}
      </span>
      <span className="absolute flex items-center justify-center w-full h-full transition-all duration-500 ease-out transform group-hover:translate-x-full ">
        {children}
      </span>
      <span className="relative invisible">{children}</span>
    </Button>
  );
};

//======================================
export const Button_v6 = ({ children, ...rest }: ButtonProps) => {
  return (
    <div className={`rounded-sm border dark:border-zinc-200 border-zinc-600 group ${rest.className}`}>
      <Button
        {...rest}
        className={cn(
          'scale-y-[.88] scale-x-[0.94] group-hover:scale-100 group-hover:m-0 duration-300 font-semibold rounded-sm transition dark:bg-zinc-50 bg-zinc-950 text-zinc-100 dark:text-zinc-800 w-full',
        )}
      >
        {children}
      </Button>
    </div>
  );
};