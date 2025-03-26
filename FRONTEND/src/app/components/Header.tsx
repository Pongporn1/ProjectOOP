import Link from "next/link";
import { TransitionLink } from "./HOC/TransitionLink";

const Header = () => (
    <header>
        <div className="header-items flex flex-row-reverse gap-5">
        <div className="header-item">
                <TransitionLink href="/">Home</TransitionLink>
            </div>
            <div className="header-item">
                <TransitionLink href="/pages/setting">Setting</TransitionLink>
            </div>
        </div>

    </header>
)

export default Header