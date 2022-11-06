const React = require('react'); (1)
const ReactDOM = require('react-dom'); (2)
const client = require('./client'); (3)

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = { fileWithLinks: [] };
    }

    componentDidMount() {
        client({ method: 'GET', path: '/links' }).done(response => {
            this.setState({ fileWithLinks: response.fileWithLinks });
        });
    }

    render() {
        return (
            <div>
                {/* <Form /> */}
                <FileWithLinksList fileWithLinks={this.state.fileWithLinks} />
            </div>
        )
    }
}

class Form extends React.Component {
    render() {
        <div>
            <form method="POST" enctype="multipart/form-data" action="/upload">
                <table>
                    <tr>
                        <td>File to upload:</td>
                        <td><input id="file" type="file" name="file" onchange="validateFileSizeAndEnableUpload()" accept=".pdf" /></td>
                        {/* <td><input id="file" type="file" name="file" onchange="validateFileSizeAndEnableUpload()" accept=".pdf" /></td> */}
                    </tr>
                    <tr>
                        <td></td>
                        <td><input type="submit" value="Upload" id="upload-button" /></td>
                    </tr>
                </table>
            </form>
        </div>
    }
}

class FileWithLinksList extends React.Component {
    render() {
        const employees = this.props.fileWithLinks.map(fileWithLink =>
            <FileWithLink key={fileWithLink.fileLink} fileWithLink={fileWithLink} />
        );
        return (
            <table>
                <tbody>
                    <tr>
                        <th>Original Name</th>
                        <th>Link</th>
                        <th>Preview</th>
                    </tr>
                    {employees}
                </tbody>
            </table>
        )
    }
}

class FileWithLink extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.fileWithLink.fileName}</td>
                <td>{this.props.fileWithLink.fileLink}</td>
                <td>{this.props.employee.description}
                    <embed src={this.props.employee.description} width="100" height="100" alt="pdf"
                        pluginspage="http://www.adobe.com/products/acrobat/readstep2.html"></embed>
                </td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)