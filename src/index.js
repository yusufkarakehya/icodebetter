import React from 'react'
import ReactDOM from 'react-dom'
import { XDropdownTreeSelect, XDropdownMultiTreeSelect } from './react/lib/XDropdownTreeSelect';
var a = {
    label: 'searchme1',
    value: 'searchme1',
    children: [
        {
            label: 'searchme2',
            value: 'searchme2',
            children: [
                {
                    label: 'searchme3',
                    value: 'searchme3',
                },
                {
                    label: 'searchme3.2',
                    value: 'searchme3.2',
                },
            ],
        },
        {
            label: 'searchme2.2',
            value: 'searchme2.2',
            children: [
                {
                    label: 'searchme5',
                    value: 'searchme5',
                },
                {
                    label: 'searchme5.2',
                    value: 'searchme5.2',
                },
            ],
        },
    ],
}

class PropsChanger extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            value:
            {
                checked: true,
                hide: false,
                label: "searchme2",
                value: "searchme2",
            }

        }
    }
    componentDidMount() {
        // setTimeout(() =>{
        //     alert('hello')
        //     this.setState({value: a})
        // },8000)
    }
    render() {
        return (
            <div>
                <XDropdownMultiTreeSelect defaultList={a} value={this.state.value} onChange={(a) => { console.log(a); this.setState({ value: a }) }} />
                <XDropdownTreeSelect defaultList={a} value={this.state.value} onChange={(a) => { console.log(a); this.setState({ value: a }) }} />
            </div>
        )
    }
}

ReactDOM.render(
    <PropsChanger />, document.getElementById("root"));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
// serviceWorker.unregister();
