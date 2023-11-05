package com.perikov.osgi.http4s.whiteboard.server.commands

import com.perikov.osgi.http4s.whiteboard.server.*
import org.apache.karaf.shell.support.table.*
import org.apache.karaf.shell.api.action
import action.*
import action.lifecycle.*
@Service
@Command(
  scope = "http4s",
  name = "list",
  description = "List of registered Http4sIORoutesProvider s"
)
class List extends Action:
  @Reference
  var registry: ProviderRegistry = compiletime.uninitialized
  override def execute(): Object =
    val table = ShellTable()
    import table.*
    val keys = Seq("service.bundleid", "service.id", "component.name")
    table.column("path")
    keys.foreach(table.column)
    registry.foreach(info =>
      addRow().addContent((info.path +: keys.map(info.props.apply))*)
    )
    table.print(System.out)
    null
