"use client";
import Link, {LinkProps} from "next/link";
import React, { ReactNode } from "react";
import { useRouter } from "next/navigation";

interface TransitionLinkProps extends LinkProps{
    children: ReactNode;
    href: string
}

export const TransitionLink = ({children, href, ...props}:TransitionLinkProps) => {

    const router = useRouter()
    const handleTransition = async (
        e:React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
        e.preventDefault();
        router.push(href);}
    

    return (<Link onClick={handleTransition} href={href} {...props}>
        {children}
        </Link>)

}