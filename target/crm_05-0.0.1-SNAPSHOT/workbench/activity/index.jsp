<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


    <script type="text/javascript">

	$(function(){
		pageList(1,2);
		//日历控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#addBtn").click(function () {
			//走后台，查询用户列表
			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"GET",
				dataType:"json",
				success:function (data){
					var html = "<option></option>"
					//遍历出来的每个n，就是一个user对象
					$.each(data,function (i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#create-owner").html(html);

					//将当前用户默认显示在用户列表中
					//在js中使用el表达式，el表达式一定要套在字符串中
					var id = "${user.id}"
					$("#create-owner").val(id);
					//展现模态窗口
					$("#createActivityModal").modal("show");
				}
			})
		})


		//为保存按钮绑定点击事件,执行添加操作
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/activity/save.do",
				data:{
					"owner":$.trim($("#create-owner").val()),
					"name":$.trim($("#create-name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val())

				},
				type:"POST",
				dataType:"json",
				success:function (data){
					//data:{"success":true/false}
					if(data.success){
						//局部刷新市场活动信息表
						//pageList(1,2);
						/**
						 * $("#activityPage").bs_pagination('getOption', 'currentPage')
						 * 表示展示第几页
						 * $("#activityPage").bs_pagination('getOption', 'rowsPerPage')
						 * 表示每页展示用户设置好的记录数
						 * */
						pageList(1
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭添加操作的模态窗口
						$("#createActivityModal").modal("hide");

						//清空模态窗口中填写的数据
						$("#activityAddForm")[0].reset();
					}else {
						alert("添加市场活动失败");
					}
				}
			})
		})


		//为全选复选框绑定点击事件,触发全选操作
		$("#all").click(function () {
			$("input[name=choose]").prop("checked",this.checked);
		})

		//绑定点击事件，实现所有choose复选框全选时，all复选框也全选
		$("#activityBody").on('click',$("input[name=choose]"),function (){
			$("#all").prop('checked',$("input[name=choose]").length==$("input[name=choose]:checked").length);
		})
		
		//为查询按钮绑定点击事件
		$("#searchBtn").click(function () {
			//点击查询按钮时，将文本框内容保存到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,2);
		})

		$("#deleteBtn").click(function () {

			//找到被选中的复选框的jQuery对象
			var checked = $("input[name=choose]:checked");
			if(checked.length==0){
				alert("请勾选要删除的记录!");
			}else {
				if(confirm("确定要删除选中的记录吗？")){
					//拼接参数
					var param = "";
					//遍历每一个已勾选的复选框对象，取value值，就取得了要删除的对象的id
					for(var i=0;i<checked.length;i++){

						param += "id="+$(checked[i]).val();

						if(i<checked.length-1){
							param += '&';
						}
					}

					$.ajax({
						url:"workbench/activity/delete.do",
						data:param,
						type:"GET",
						dataType:"json",
						success:function (data){

							//data:{"success":true/false}
							if(data.success){
							//局部刷新市场活动信息表
								pageList(1
										,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


							}else{

								alert("删除市场活动失败");

							}
						}
					})
				}

			}
		})

		//为修改按钮绑定点击事件，打开修改模态窗口
		$("#editBtn").click(function (){
			var checked = $("input[name=choose]:checked")
			if(checked.length==0){
				alert("请选择要修改的记录!");
			}else if(checked.length>1){
				alert("只能选择一条记录进行修改");
			}else {
				var id = checked.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{
						id:id
					},
					type:"GET",
					dataType:"json",
					success:function (data){
						/*
						* data:用户列表+市场活动对象
						* {"userList:[{user1},{user2},...],"activity":{}}
						* */
						//下拉列表
						var html = "<option></option>"
						$.each(data.userList,function (i,n){
							html += "<option value='"+n.id+"'>"+n.name+"</option>"
						})
						$("#edit-owner").html(html);

						//处理单条活动记录
						$("#edit-id").val(data.activity.id);
						$("#edit-name").val(data.activity.name);
						$("#edit-owner").val(data.activity.owner);
						$("#edit-startDate").val(data.activity.startDate);
						$("#edit-endDate").val(data.activity.endDate);
						$("#edit-cost").val(data.activity.cost);
						$("#edit-description").val(data.activity.description);

						//所有内容填充完毕后，打开修改模态窗口
						$("#editActivityModal").modal("show");
					}
				})
			}
		})

		//为修改按钮绑定点击事件，执行记录的修改操作
		$("#updateBtn").click(function(){
			$.ajax({
				url:"workbench/activity/update.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-owner").val()),
					"name":$.trim($("#edit-name").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-description").val())

				},
				type:"POST",
				dataType:"json",
				success:function (data){
					//data:{"success":true/false}
					if(data.success){
						//局部刷新市场活动信息表
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭修改操作的模态窗口
						$("#editActivityModal").modal("hide");
					}else {
						alert("修改市场活动失败");
					}
				}
			})
		})
	});
	/*
	对于所有关系型数据库，做前端分页相关操作的基础组件
	就是pageNo和pageSize
	需要调用pageList方法的情况：
	1.点击左侧菜单栏"市场活动"超链接，需要刷新市场活动列表
	2.添加、修改、删除后需要刷新
	3.点击查询按钮时需要刷新
	4.点击分页组件的时候需要刷新
	 */

	//调用分页查询方法时，从后台发送ajax请求，并根据响应的信息局部刷新页面
	function pageList(pageNo,pageSize){

		//将全选复选框取消勾选
		$("#all").prop("checked",false);

		//查询前，从隐藏域中取出数据填充到输入框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"search-name":$.trim($("#search-name").val()),
				"search-owner":$.trim($("#search-owner").val()),
				"search-startDate":$.trim($("#search-startDate").val()),
				"search-endDate":$.trim($("#search-endDate").val())
			},
			type:"GET",
			dataType:"json",
			success:function (data){
				//data:{"totalList":条数,"dataList":[{activity1,act..2,....}]}
				//每一个n就是一个activity对象
				var html;
				$.each(data.dataList,function (i,n) {

						html += '<tr class="active"> ';
						html += '<td><input type="checkbox" name="choose" value="'+n.id+'" /></td>';
						html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
						html += '<td>'+n.owner+'</td>';
						html += '<td>'+n.startDate+'</td>';
						html += '<td>'+n.endDate+'</td>';
						html += '</tr>';

				});
				$("#activityBody").html(html);

				//计算总页数
                var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

                //在pageList.do处理ajax返回值后,加入分页组件
                $("#activityPage").bs_pagination({
                    currentPage: pageNo, // 页码
                    rowsPerPage: pageSize, // 每页显示的记录条数
                    maxRowsPerPage: 20, // 每页最多显示的记录条数
                    totalPages: totalPages, // 总页数
                    totalRows: data.total, // 总记录条数

                    visiblePageLinks: 3, // 显示几个卡片

                    showGoToPage: true,
                    showRowsPerPage: true,
                    showRowsInfo: true,
                    showRowsDefaultInfo: true,

                    onChangePage : function(event, data){
                        pageList(data.currentPage , data.rowsPerPage);
                    }
                });

            }
		})
	}
	

	
</script>
</head>
<body>
<%--	隐藏域保存点击查询后输入框的内容，每次传给后台的数据从这里拿--%>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id">

						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="all"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

                <div id="activityPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>