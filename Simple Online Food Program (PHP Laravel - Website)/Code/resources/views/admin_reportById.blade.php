<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>

    <script src="{{asset('/js/app.js')}}"></script>
    <script src="DataTables-1.10.20/js/jquery.dataTables.js"></script>
    <script src="DataTables-1.10.20/js/dataTables.bootstrap4.min.js"></script>
    <script type="text/javascript" src="DataTables-1.10.20/js/jquery.dataTables.js"></script>

    <style>
        
    </style>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
    <script>
        $(document).ready(function () {
            $('#data_merchant').DataTable({
                "pagingType": "simple"
            });
            $('.dataTables_length').addClass('bs-select');
        });
    </script>
    @include('inc.navbar_admin')
    <main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
            <h4>Laporan Ke {{$report->Id_merchant}} dari {{$report->Reported_by}}</h4>
            <hr>
            <img src='{{asset("/images/report/".$report->Foto_bukti.".jpg")}}' alt="Image not found" width="100%" height="100%">
            <hr>
            {{$report->Message}}
		</div>
	</main>
    @include('inc.footer')	
</body>
</html>