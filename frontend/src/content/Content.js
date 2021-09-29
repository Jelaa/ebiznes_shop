import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../components/SignIn";
import SignUp from "../components/SignUp";
import Main from "../components/Main";
import PublicRoute from "../route/PublicRoute";

function Content() {
    return (
        <div className="Content">
            <Switch>
                <PublicRoute path="/signIn"><SignIn/>
                </PublicRoute>
                <PublicRoute path="/signUp"><SignUp/>
                </PublicRoute>
                <Route path="/">
                    <Main />
                </Route>
            </Switch>
        </div>
    )
}

export default Content;