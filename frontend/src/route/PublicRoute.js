import React from "react";
import {useAuth} from "../contexts/AuthContext";
import {Redirect, Route} from "react-router";

function PublicRoute({children, ...rest}) {
    const {checkIfLogged} = useAuth();
    return (
        <Route
            {...rest}
            render={({location}) =>
                checkIfLogged() ? (
                    <Redirect
                        to={{
                            pathname: "/",
                            state: {from: location}
                        }}
                    />
                ) : (
                    children
                )
            }
        />
    )
}

export default PublicRoute;