import React from 'react'
import { XDropdownTreeSelect, XDropdownMultiTreeSelect, XDropdownTreeSelectResult } from './';
import { Form, FormGroup, Label, Input } from 'reactstrap';
// import "../css/styles.alba.css";
// import "../css/iwb-nifty.css";
// import "../css/style.css";
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

export class DropdownTreeSelectDemo extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            value:
            {
                "checked": "true",
                "hide": "false",
                "label": "searchme2",
                "value": "searchme2"
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
                <Form>
                    <FormGroup row>
                        <Label for="exampleEmail" sm={2} size="lg">Email</Label>

                        <Input type="email" name="email" id="exampleEmail" placeholder="lg" bsSize="lg" />

                    </FormGroup>
                </Form>
                <XDropdownMultiTreeSelect defaultList={a} value={this.state.value} onChange={(a) => { console.log(a); this.setState({ value: a }) }} />
                <XDropdownTreeSelect defaultList={a} value={this.state.value} onChange={(a) => { console.log(a); this.setState({ value: a }) }} />
                <XDropdownTreeSelectResult defaultList={a} value={this.state.value} onChange={(a) => { console.log(a); this.setState({ value: a }) }} />
            </div>
        )
    }
}