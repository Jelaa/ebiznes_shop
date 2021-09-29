import React, {Component} from 'react';
import './App.css';
import { BrowserRouter} from "react-router-dom";
import ShopName from "./components/ShopName";
import {AuthContextProvider} from "./contexts/AuthContext";
import Auth from "./components/Auth";
import Content from "./content/Content";

class App extends Component {
    render() {
        return (
            <BrowserRouter>
                <AuthContextProvider>
                    <ShopName/>
                    <Auth/>
                    <Content/>
                </AuthContextProvider>
            </BrowserRouter>
        );
    }
}

export default App;