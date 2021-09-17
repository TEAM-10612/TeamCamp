import logo from './logo.svg';
import './App.css';
import './index.css'
import {useState, useEffect, Component} from "react";
import {Button} from "react-bootstrap";
import {Link} from "react-router-dom";



class App extends Component{
    render() {
        return(
            <div className="App">
                <Button><Link to = "/api/version1">CAMPING_GO</Link></Button>
            </div>
        )
    }
}

export default App;
