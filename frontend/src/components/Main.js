import React from "react";
import {useAuth} from "../contexts/AuthContext";
import ShopName from "./ShopName";

function Main() {
    const {checkIfLogged} = useAuth()

    return (
        checkIfLogged() ? <ShopName /> : null
    )
}

export default Main;