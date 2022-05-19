# ![Image](https://www.vernalis.com/wp-content/uploads/2020/12/Vern_HitGen_V4-1-300x138.png)

This repository is the source code for the Vernalis KNIME community contribution.  
For more details see our [contribution home page](https://www.knime.com/book/vernalis-nodes-for-knime-trusted-extension) or our recent publication describing the historical development - '[Five years of the KNIME Vernalis Cheminformatics Community Contribution](https://dx.doi.org/10.2174/0929867325666180904113616)'

### Installation
To install the nodes and other feature in the KNIME analytics platform, please follow the instructions at https://www.knime.com/community or at https://hub.knime.com/vernalis/extensions/com.vernalis.knime.feature/latest

### Examples
There are some workflows available on our [project page](https://hub.knime.com/vernalis/extensions/com.vernalis.knime.feature/latest) on the KNIME hub and also on the examples server in the KNIME desktop analytics platform

### Contributing / Bugs / Feature Requests
We are currently unable to accept pull requests.
If you spot and bugs or enhancements you would like please contact us via our page on the [KNIME Community Forum](https://forum.knime.com/c/community-extensions/vernalis).  Bugs can also be reported via the Issue Tracker of this repository.

### Citing this work
If you use our nodes in your published work, please cite us using the above review article and stating the version number in the form `major.minor.patch`, e.g. 1.27.0

### Contacting us
If you want to ask us questions about how to use one of our nodes, suggestions for new features in our nodes, bugs, or just to tell us you are using our code, we can be contacted via our page on the [KNIME Community Forum](https://forum.knime.com/c/community-extensions/vernalis). We cannot take 'orders' or commissions for new nodes, but if you would like a node which you think has functionality related to our existing public nodes, please do get in touch - it's possible that we already have a node to do what you want which has not been released publicly, and we may be able to get it released for you.

### Changelog
There is a full changelog for each version available [here](CHANGELOG.md)

### API
The core plugin (`com.vernalis.knime.core`) exposes a number of classes which may be of use to node developers.

Additionally, since v1.32.0, we have exposed two extension points:

* `com.vernalis.knime.flowcontrol.variablecondition` (Variable Condition) - Used to provide conditions to the Flow Variable value based nodes
* `com.vernalis.knime.flowcontrol.porttypecombiner` (Port Type Combiner) - Used to allow merging of multiple active ports in Configurable End IF/Case nodes

It is possible that some minor changes to those extension points will be made in coming versions - if you intend to implement them, then please do [let us know](#contacting-us)