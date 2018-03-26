<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Schedule</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css"></head>
<body>

    <h2>Schedule</h2>

    <table class="data-schedule-js table table-striped" >
        <tr>
            <th>Date</th>
            <th>Shift1</th>
            <th>Shift2</th>
        </tr>
    </table>

    <button id="fetchSchedule" class="btn btn-default" type="submit">Fetch</button>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script type="text/javascript">

        $("#fetchSchedule").bind("click", function() {

            $.get("http://localhost:8080/swf/schedule/get", function(data) {

                $.each(data.schedules, function(i, schedule) {

                    $(".data-schedule-js").append(
                        "<tr><td>" + schedule.date + "</td>" +
                        "<td>" + schedule.shifts[0].engineer.empId + ", " + schedule.shifts[0].engineer.name + ", " + schedule.shifts[0].engineer.phone + "</td>" +
                        "<td>" + schedule.shifts[1].engineer.empId + ", " + schedule.shifts[1].engineer.name + ", " + schedule.shifts[1].engineer.phone + "</td></tr>");
                });

            });
        });
    </script>
</body>
</html>