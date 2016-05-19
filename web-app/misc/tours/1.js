(function() {

    var tour = Tour.create()

    tour

        .setGrailsPage('canvas', 'editor')

        .beforeStart(function(cb) {
            SignalPath.clear()
            $('#beginDate').val('2016-04-11')
            $('#endDate').val('2016-04-12')
            cb()
        })

        .step("Hello again! In this (second) tutorial, we'll see how to build logic around incoming data by connecting various " +
            "modules together. <br/><br/>Remember that you can restart the tours from the <code>?</code> menu, as well as find " +
            "tutorial videos and examples there. "+
            "<br/><br/>Click Next when you are ready to begin!", '#navHelpLink',
            { placement: 'left', animated: true })

        .step("Let's start by adding the Public transport demo data stream as was done in the 1st tutorial. <br/><br/>"+
            'Search for <b>Public transport demo</b>, and add it to the canvas by selecting it in the results. Do this now.',
            '#search',
            function() {
                function listener(e, jsonData, div) {
                    if (jsonData.name === 'Stream' && jsonData.params[0].streamName === 'Public transport demo') {
                        tour.bindModule(jsonData.name, div)
                        $(SignalPath).off('moduleAdded', listener)
                        tour.next()
                    }
                }

                $(SignalPath).on('moduleAdded', listener)
            }
        )

        .step("For the duration of this tutorial, we'll be focusing on a single, specific tram. To achieve this goal, " +
            "we need to filter the stream's incoming data appropriately.",
            '.tourStream1'
        )

        .step(" Open the <b>Utils</b> section by clicking it to list utility-related modules.",
            '#moduleTree',
            function() {
                $('.jstree a:contains(Utils)').parent().one('click', tour.next)
            }
        )

        .step('Under <b>Utils</b>, find the <code>Filter</code> module and drag and drop it to the canvas.',
            '#moduleTree',
            tour.waitForModuleAdded('Filter')
        )

        .step("Next, open the <b>Text</b> section by clicking it to list text processing-related modules.",
            '#moduleTree',
            function() {
                $('.jstree a:contains(Text)').parent().one('click', tour.next)
            }
        )

        .step('Under <b>Text</b>, find the <code>TextEquals</code> module and drag and drop it to the canvas.',
            '#moduleTree',
            tour.waitForModuleAdded('TextEquals')
        )

        .step("Our goal is to filter data related to a single tram (specifically, vehicle RHKL00122) from all tram data being pushed out by <b>Stream</b>. To do so, we will need to pass forward only those events whose <b>veh</b> equals <b>RHKL00122</b>.",
            '.tourStream1'
        )

        .step("Let's make it happen!<br/><br/>"+
            "Start dragging from the highlighted circle near the output <code>veh</code> on the Stream, and drop on the second input (called <code>text</code>) on the TextEquals module.",
            '.tourStream1',
            { placement: 'bottom' },
            tour.highlightOutputUntilDraggingStarts("tourStream1.veh")
        )

        .step("Drop the connection on the second input of the TextEquals, called <code>text</code>.",
            '.tourTextEquals1 .endpoint.input:nth(1)',
            function(cb) {
                tour.waitForConnections([['tourStream1.veh', 'tourTextEquals1.text']])(cb)
            }
        )

        .step("Next connect <code>equals?</code> of TextEquals to <code>pass</code> of Filter.",
            '.tourTextEquals1 .endpoint.output:first',
            { placement: 'bottom' },
            tour.highlightOutputUntilDraggingStarts("tourTextEquals1.equals?")
        )

        .step("Drop the connection on the first input of the Filter, called <code>pass</code>.",
            '.tourFilter1 .endpoint.input:first',
            { placement: 'top' },
            function(cb) {
                tour.waitForConnections([['tourTextEquals1.equals?', 'tourFilter1.pass']])(cb)
            }
        )

        .step("Now let's define the vehicle we are filtering.<br><br>Type <code>RHKL00122</code> into parameter <b>search</b> of module <b>Filter</b>.",
            '.tourTextEquals1 .endpoint.parameter:first',
            tour.waitForInput(".tourTextEquals1 .parameterInput", "RHKL00122")
        )

        .step("Then connect <code>lat</code>, <code>long</code>, and <code>spd</code> of Stream to <code>in1</code>, <code>in2</code>, and <code>in3</code> of module Filter",
            '.tourStream1',
            function(cb) {
                tour.waitForConnections([
                    ['tourStream1.lat', 'tourFilter1.in1'],
                    ['tourStream1.long', 'tourFilter1.in2'],
                    ['tourStream1.spd', 'tourFilter1.in3']
                ])(cb)
            }
        )

        .step("Let's add a <code>Table</code> to see affirm that data is flowing in and to see how it looks like",
            '#search',
            tour.waitForModuleAdded('Table')
        )

        .step("Connect <code>out1</code>, <code>out2</code>, and <code>out3</code> of Filter to Table (in that order)",
            '.tourStream1',
            function(cb) {
                tour.waitForConnections([
                    ['tourFilter1.out1', 'tourTable1.in1'],
                    ['tourFilter1.out2', 'tourTable1.in2'],
                    ['tourFilter1.out3', 'tourTable1.in3']
                ])(cb)
            }
        )

        .step("Pretty easy, right?")

        .offerNextTour("Great job! In the next tour, we'll develop this a bit further. Click Begin when you are ready!")

        .ready()

})()
