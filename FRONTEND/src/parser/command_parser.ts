import { left, Either, isRight, right } from "fp-ts/lib/Either"



export const parse = (tokens: string[]) => {


}

const parse_commmand = (tokens: string[], result: Either<string, string[]>) => {
    if(tokens.length > 0 && isRight(result)){
        if(tokens[0][0] === "m"){
            const res1 = parse_select_command(tokens, result)
            if(res1.tokens.length > 0) return parse_edit_command(res1.tokens, res1.result)
            else return res1
        }else {
            return parse_edit_command(tokens, result)
        }
    }else{
        return {tokens: tokens, result:left("Not Enought Token")}
    }
}

const parse_select_command = (tokens: string[], result: Either<string, string[]>) => {
    if(tokens.length > 0 && isRight(result)){
        const tk = tokens.slice(1)
        const res = [...result.right, tokens[0]]
        return {tokens: tk, result: right(res)}
    }else{
        return {tokens: tokens, result:left("Not Enought Token")}
    }
}

const parse_edit_command = (tokens: string[], result: Either<string, string[]>) => {
    if(tokens.length > 0 && isRight(result)){
        
    }else{
        return {tokens: tokens, result:left("Not Enought Token")}
    }
}

