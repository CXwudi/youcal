package mikufan.cx.yc.core.ical.util

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent

fun VEvent.addProperty(property: Property) = add<VEvent>(property)
fun VEvent.addComponent(component: Component) = add<VEvent>(component)
fun VAlarm.addProperty(property: Property) = add<VAlarm>(property)
fun VAlarm.addComponent(component: Component) = add<VAlarm>(component)
fun Calendar.addComponent(component: CalendarComponent) = add<Calendar>(component)
fun Calendar.addProperty(property: Property) = add<Calendar>(property)
