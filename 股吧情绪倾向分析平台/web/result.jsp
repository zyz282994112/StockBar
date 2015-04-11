<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="bootstrap.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />
<script src="ichart.js"></script>

<!--[if IE 6]><link href="default_ie6.css" rel="stylesheet" type="text/css" /><![endif]-->

</head>
<body>

<div id="page" class="container">
	<div id="header">
		<div id="logo">
			<img src="images/logo.jpg" alt="" />
			<h2><a href="#">股吧情绪量化分析平台</a></h2>
			<span>Design by <a href="http://templated.co" rel="nofollow">张一舟</a></span>
		</div>
		<div id="menu">
			<ul>
				<li><a href="index.jsp" accesskey="1" title="">数据爬取</a></li>
                <li><a href="qxlh.jsp" accesskey="3" title="">情绪量化</a></li>
                <li class="current_page_item"><a href="tbzs.jsp" accesskey="4" title="">图表展示</a></li>
                <li><a href="sjxl.jsp" accesskey="2" title="">数据训练</a></li>
                <li><a href="#" accesskey="5" title="">扩展....</a></li>
			</ul>
		</div>
	</div>
	<div id="main">
		<div id="welcome">
			<div class="title">
				<h2>图表展示</h2>
				<span class="byline">展示您选取的股票在所在时间段内的日发帖量变化及情绪倾向走势</span>
			</div>
        </div>
            <div id='canvasDiv'></div>

        <form action="ResultServlet.do">
            <input type="file" name="filename" >
            <input type="submit">
        </form>
		<div id="copyright">
			<span>&copy; Yizhou. All rights reserved. | PHONE 18801734831</span>
			<span>Design by <a href="http://templated.co" rel="nofollow">Yizhou</a>.</span>
		</div>
        <div id="pTitle"></div>

	</div>
</div>
</body>
</html>
<script>
    $(function(){
        <%
         FileReader reader = new FileReader("D:\\股吧\\evaluation\\"+request.getParameter("filename")); // 建立一个输入流对象reader
         BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
         String line = br.readLine();
         %>


        var data = new Array();
        var data1 = new Array();
        <%
        String xx="";
        while(line!=null){
         String[] tmp = line.split("\\t");
         xx += tmp[2]+",";
        line=br.readLine();
        %>
        data.push({name : '<%=tmp[0]%>',value : <%=Integer.parseInt(tmp[1])%>,color:'#F7D358'});

        <% }
        %>
        data1.push({name : 'emotion',value : [<%
                    String[] num = xx.split(",");

                    for (int i=0;i<num.length-1;i++) {
                    if(num[i].equals("NaN"))
                        num[i]="0.0";
                       else if(num[i].equals("Infinity"))
                        num[i]="1.0";
                        else if(num[i].equals("-Infinity"))
                        num[i]="-1.0";
                    %>

            <%=Double.parseDouble(num[i])%>,
            <% } %>
            <%
            if(num[num.length-1].equals("NaN"))
                        num[num.length-1]="0.0";
                       else if(num[num.length-1].equals("Infinity"))
                        num[num.length-1]="20.0";
                        else if(num[num.length-1].equals("-Infinity"))
                        num[num.length-1]="-20.0";

            %><%=Double.parseDouble(num[num.length-1])%>] ,color:'#aa4643',line_width:3});
        //是否启用动画
        var animation = true;

        var chart = new iChart.Column3D({
            render : 'canvasDiv',
            data: data,
            title : {
                text : '情绪倾向量化结果展示',
                color : '#3e576f'
            },
            subtitle : {
                text : '柱状图为日发帖量统计，折线图为情绪倾向变化',
                color : '#6d869f'
            },
            footnote : {
                text : '日期',
                color : '#909090',
                fontsize : 11,
                padding : '0 38'
            },
            width : 700,
            height : 400,
            animation : animation,
            animation_duration:600,
            shadow : true,
            shadow_blur : 2,
            shadow_color : '#aaaaaa',
            shadow_offsetx : 1,
            shadow_offsety : 0,
            zScale:0.5,
            xAngle : 50,
            bottom_scale:1.1,
            column_width:90,
            label:{
                color:'#4c4f48'
            },
            sub_option:{
                label : {
                    color : '#4c4f48'
                },
                listeners:{
                    parseText:function(r,t){
                        //自定义柱形图上方label的格式。
                        return t;
                    }
                }
            },
            text_space : 16,//坐标系下方的label距离坐标系的距离。
            coordinate:{
                background_color : '#d5d8d1',
                grid_color : '#919d7e',
                color_factor : 0.24,
                board_deep:10,//背面厚度
                pedestal_height:10,//底座高度
                left_board:true,//左侧面板
                height:'90%',
                width:'80%',
                shadow:true,//底座的阴影效果
                wall_style:[{//坐标系的各个面样式
                    color : '#333333'
                },{
                    color : '#b2b2d3'
                }, {
                    color : '#a6a6cb'
                },{
                    color : '#333333'
                },{
                    color : '#74749b'
                },{
                    color : '#a6a6cb'
                }],
                scale:[{
                    position:'left',
                    start_scale:0,
                    scale_space:100,
                    end_scale:1000,
                    label:{
                        color:'#4c4f48'
                    },
                    listeners:{
                        parseText:function(t,x,y){
                            //自定义左侧坐标系刻度文本的格式。
                            return {text:t}
                        }
                    }
                },{
                    position:'right',
                    start_scale:0,
                    scale_space:100,
                    end_scale:1000,
                    scaleAlign:'right',
                    label:{
                        color:'#aa4643'
                    },
                    listeners:{
                        parseText:function(t,x,y){
                            //自定义右侧坐标系刻度文本的格式。
                            return {text:t}
                        }
                    }
                }]
            }
        });


        var line = new iChart.LineBasic2D({
            z_index:1000,
            data: data1,
            label:{
                color:'#4c4f48'
            },
            point_space:chart.get('column_width')+chart.get('column_space'),
            tip:{
                enable:true,
                listeners:{
                    //tip:提示框对象、name:数据名称、value:数据值、text:当前文本、i:数据点的索引
                    parseText:function(tip,name,value,text,i){
                        return "情绪倾向:"+value;
                    }
                }
            },
            scaleAlign : 'right',
            sub_option : {
                smooth:true,
                label:false,
                hollow_inside:false,
                point_size:14
            },
            animation : animation,
            coordinate:chart.coo
        });

        chart.plugin(line);

        //利用自定义组件构造左侧说明文本。
        chart.plugin(new iChart.Custom({
            drawFn:function(){
                //计算位置
                var coo = chart.getCoordinate(),
                        x = coo.get('originx'),
                        y = coo.get('originy'),
                        H = coo.height;
                //在左侧的位置，渲染说明文字。
                chart.target.textAlign('center')
                        .textBaseline('middle')
                        .textFont('600 13px Verdana')
                        .fillText('日发帖量',x-50,y+H/2,false,'#6d869f', false,false,false,-90);

            }
        }));

        chart.draw();
    });

</script>

