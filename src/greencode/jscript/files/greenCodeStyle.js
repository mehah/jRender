Greencode.modalErro = {
	style : {
		display : 'block',
		width : '900px',
		position : 'absolute',
		top : '30%',
		left : '50%',
		marginLeft : '-450px',
		marginTop : '-125px',
		border : '1px solid black',
		backgroundColor : '#d5e9e2',
		zIndex : '99999'
	},
	topBar : {
		style : {
			width : '100%',
			fontSize : '12px',
			backgroundColor : '#4099ff'
		},
		title : {
			style : {
				margin : '0px 5px',
				width : '97%',
				position : 'relative',
				styleFloat : 'left', /* IE */
				cssFloat : 'left'
			}
		},
		closeButton : {
			style : {
				cursor : 'pointer',
				fontWeight : 'bold'
			}
		}
	},
	content : {
		style : {
			fontSize : '13px',
			maxHeight : '400px',
			overflowY : 'scroll'
		},
		title : {
			style : {
				padding : '5px 15px',
				color : '#297fa5'
			}
		},
		lineClass : {
			style : {
				margin : '5px 40px',
				color : '#7573ce'
			}
		},
		possibleErro : {
			style : {
				margin : '5px 40px',
				color : 'red'
			}
		}
	}
};