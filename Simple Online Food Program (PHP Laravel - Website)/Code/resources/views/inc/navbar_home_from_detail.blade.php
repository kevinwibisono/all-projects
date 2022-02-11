<nav class="navbar navbar-expand-md navbar-dark bg-dark">
        <a class="navbar-brand" href="#">Catering is Fun</a>

        <div class="collapse navbar-collapse" id="navbarsExampleDefault">
            <ul class="navbar-nav mr-auto">
            </ul>

            <ul class="navbar-nav navbar-right">
                <li>
                    <form action="{{url('/to_home_customer')}}" method="post" onsubmit="return confirm('If you change restaurant, all cart will be discarded, are you sure you want to change restaurant?');">
                        {{csrf_field()}}
                        <button type='submit' style="color:white" class="btn btn-outline-primary">Home</button><br>
                    </form>
                </li>
            </ul>
        </div>
    </nav>
