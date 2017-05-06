Greencode.modalErro = {
	show: function(stacktrace) {
		var divGreenCodeModalErro = document.createElement("div"),
			spanTitulo = document.createElement("span"),
			spanBotaoFechar = document.createElement("span"),
			topBar = document
			.createElement("div"),
			contentModalError = document.createElement("div");

		divGreenCodeModalErro.setAttribute('id', 'GreenCodemodalErro');
		for (var i in Greencode.modalErro.css.style)
			divGreenCodeModalErro.style[i] = Greencode.modalErro.css.style[i];

		spanTitulo.appendChild(document.createTextNode('Exception:'));
		for (var i in Greencode.modalErro.css.topBar.title.style)
			spanTitulo.style[i] = Greencode.modalErro.css.topBar.title.style[i];

		spanBotaoFechar.appendChild(document.createTextNode('X'));
		for (var i in Greencode.modalErro.css.topBar.closeButton.style)
			spanBotaoFechar.style[i] = Greencode.modalErro.css.topBar.closeButton.style[i];

		spanBotaoFechar.registerEvent('click', function() {
			divGreenCodeModalErro.parentNode.removeChild(divGreenCodeModalErro);
		});

		for (var i in Greencode.modalErro.css.topBar.style)
			topBar.style[i] = Greencode.modalErro.css.topBar.style[i];

		topBar.appendChild(spanTitulo);
		topBar.appendChild(spanBotaoFechar);

		divGreenCodeModalErro.appendChild(topBar);

		contentModalError.setAttribute('class', 'content');
		for (var i in Greencode.modalErro.css.content.style)
			contentModalError.style[i] = Greencode.modalErro.css.content.style[i];

		divGreenCodeModalErro.appendChild(contentModalError);

		document.body.appendChild(divGreenCodeModalErro);

		var error = stacktrace,
			title = error.className + ": " + error.message,
			divTitle = document.createElement("div");

		for (i in Greencode.modalErro.css.content.title.style)
			divTitle.style[i] = Greencode.modalErro.css.content.title.style[i];

		divTitle.appendChild(document.createTextNode(title));

		contentModalError.appendChild(divTitle);

		if (error.stackTrace != null) {
			for (var i2 in error.stackTrace) {
				var st = error.stackTrace[i2],
					msg = st.className + '.' + st.methodName + '(',
					lineDiv = document.createElement("div");

				msg += (st.lineNumber < 0 ? 'Unknown Source' : st.fileName + ':' + st.lineNumber) + ')';

				lineDiv.appendChild(document.createTextNode(msg));

				if (st.possibleError) {
					for (var i3 in Greencode.modalErro.css.content.possibleErro.style)
						lineDiv.style[i3] = Greencode.modalErro.css.content.possibleErro.style[i3];
				} else {
					for (var i3 in Greencode.modalErro.css.content.lineClass.style)
						lineDiv.style[i3] = Greencode.modalErro.css.content.lineClass.style[i3];
				}

				contentModalError.appendChild(lineDiv);
			}
		}
	},
	css: {
		style: {
			display: 'block',
			width: '900px',
			position: 'absolute',
			top: '30%',
			left: '50%',
			marginLeft: '-450px',
			marginTop: '-125px',
			border: '1px solid black',
			backgroundColor: '#d5e9e2',
			zIndex: '99999'
		},
		topBar: {
			style: {
				width: '100%',
				fontSize: '12px',
				backgroundColor: '#4099ff'
			},
			title: {
				style: {
					margin: '0px 5px',
					width: '97%',
					position: 'relative',
					styleFloat: 'left',
					/* IE */
					cssFloat: 'left'
				}
			},
			closeButton: {
				style: {
					cursor: 'pointer',
					fontWeight: 'bold'
				}
			}
		},
		content: {
			style: {
				fontSize: '13px',
				maxHeight: '400px',
				overflowY: 'scroll'
			},
			title: {
				style: {
					padding: '5px 15px',
					color: '#297fa5'
				}
			},
			lineClass: {
				style: {
					margin: '5px 40px',
					color: '#7573ce'
				}
			},
			possibleErro: {
				style: {
					margin: '5px 40px',
					color: 'red'
				}
			}
		}
	}
};