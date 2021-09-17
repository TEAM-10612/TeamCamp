import React,{Component} from "react";
import './App.css'
import AppNavbar from './AppNavbar';
import {Link} from 'react-router-dom';
import {Button , Container} from "react-bootstrap";



class Home extends Component {
    render() {
        return (
            <div className="App">
                <AppNavbar/>
                <header className="App-header">
                    <div className="App-intro">
                        <Button color="link"><Link to="/groups">Study Group</Link></Button>
                    </div>
                </header>
            </div>
        );
    }
}


export default Home;