import {motion, useAnimate, useAnimation} from "framer-motion"

const Logo = () => {
    return(
    <>
        <motion.div className='flex flex-col border-[0.2rem] py-3 px-12 text-[#E0E1DD] border-[#E0E1DD]'>
        <div className="text-center text-[4rem]">KOMBAT</div>
        <motion.div className='text-center text-[1rem] mt-[-0.5rem]'>Kickstart Offense with Minion's Best in an Amicable Territory</motion.div>
        </motion.div>
    </>)
}

export default Logo;