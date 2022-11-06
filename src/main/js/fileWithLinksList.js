const React = require("react");

export class FileWithLinksList extends React.Component {
    render() {
        let fileWithLinksComponents;

        if (this.props.fileWithLinks?.length === 0) {
            fileWithLinksComponents = <Empty/>
        } else {
            fileWithLinksComponents = this.props.fileWithLinks.map(fileWithLink =>
                <FileWithLink key={fileWithLink.fileLink} fileWithLink={fileWithLink}/>
            );
        }

        return (
            <table>
                <tbody>
                <tr>
                    <th>Original Name</th>
                    <th>Link</th>
                    <th>Preview</th>
                </tr>
                {fileWithLinksComponents}
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
                <td>
                    <button value={this.props.fileWithLink.fileLink} onClick={e => updateLink(e.target.value)}>
                        Preview
                    </button>
                </td>
            </tr>
        )
    }
}

class Empty extends React.Component {
    render() {
        return (
            <tr>
                <td colSpan="2"> No Files Available</td>
            </tr>
        )
    }
}

export class PdfRender extends React.Component {
    constructor(props) {
        super(props)
        this.state = {link: ""}
        updateLink = updateLink.bind(this)
    }

    render() {

        return (
            <embed src={this.state.link} width="1000" height="1000" alt="pdf" title={this.state.link}
                   pluginspage="http://www.adobe.com/products/acrobat/readstep2.html"></embed>
        )
    }
}

function updateLink(link) {
    this.setState({link})
}
