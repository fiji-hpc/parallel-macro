package cz.it4i.fiji.parallel_macro;

import java.lang.reflect.*;


/**
 * This class provides a variety of convenience methods for dynamically
 * calling methods and allocating classes.  This utility class simplifies
 * some of Java's reflection API and fixes some issues.
 */
public class Reflection
{
    /**
     * Given a String representing the fully qualified name of a 
     * class, returns an initialized instance of the corresponding 
     * class using default constructor.  Returns null if string 
     * does not name a valid class.
     */
    public static Object createInstance (String className)
        throws ReflectionException
    {
        try
        {
            return Class.forName(className).newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new ReflectionException("Incorrectly named class " + className);
        }
        catch (Exception e)
        {
            throw new ReflectionException("No public default constructor for " + className);
        }
    }


    /**
     * Given String representing fully qualified name of a class and the
     * actual parameters, returns initialized instance of the corresponding 
     * class using matching constructor.  
     */
    public static Object createInstance (String name, Object ... args)
        throws ReflectionException
    {
        try
        {
            Class c = Class.forName(name);
            for (Constructor current : c.getDeclaredConstructors())
            {
                Class[] formals = current.getParameterTypes();
                if (typesMatch(current, formals, args))
                {
                    return current.newInstance(convertArgs(current, formals, args));
                }
            }
            throw new ReflectionException("No matching public constructor for " + name);
        }
        catch (ClassNotFoundException e)
        {
            throw new ReflectionException("Incorrectly named class " + name);
        }
        catch (Exception e)
        {
            throw new ReflectionException("No matching public constructor for " + name);
        }
    }


    /**
     * Given a target object with a no argument method of the given name, 
     * call the named method on that object and return the result.
     * 
     * If the method's return type is void, null in returned.
     */
    public static Object callMethod (Object target, String name)
        throws ReflectionException
    {
        try
        {
            Method toCall = target.getClass().getDeclaredMethod(name, new Class[0]);
            return toCall.invoke(target, new Object[0]);
        }
        catch (Exception e)
        {
            throw new ReflectionException("No matching public method " + name + 
                                          " for " + target.getClass().getName());
        }
    }


    /**
     * Given a target object with a method of the given name that takes 
     * the given actual parameters, call the named method on that object 
     * and return the result. 
     * 
     * If the method's return type is void, null in returned.
     */
    public static Object callMethod (Object target, String name, Object ... args)
        throws ReflectionException
    {
        try
        {
            for (Method current : target.getClass().getDeclaredMethods())
            {
                if (name.equals(current.getName()))
                {
                    Class[] formals = current.getParameterTypes();
                    if (typesMatch(current, formals, args))
                    {
                        return current.invoke(target, convertArgs(current, formals, args));
                    }
                }
            }
            throw new ReflectionException("No matching public method " + name +
                                          " for " + target.getClass().getName());
        }
        catch (Exception e)
        {
            throw new ReflectionException("No matching public method " + name +
                                          " for " + target.getClass().getName());
        }
    }


    /**
     * Given a target object with an instance variable with the given name,
     * get the value of the named variable on that object and return it.
     */
    public static Object getFieldValue (Object target, String name)
        throws ReflectionException
    {
        try
        {
            return target.getClass().getDeclaredField(name).get(target);
        }
        catch (Exception e)
        {
            throw new ReflectionException("No matching public instance variable for " + 
                                          target.getClass().getName());
        }
    }


    // are parameters of compatible types and in same order?
    private static boolean typesMatch (Member function, Class[] formals, Object[] actuals)
    {
        if ((actuals.length == formals.length) || 
            (actuals.length >= formals.length && isVarArgs(function)))
        {
            int idx = 0;
            // check each parameter individually
            for ( ; idx < formals.length - 1; idx++)
            {
                if (! isInstance(formals[idx], actuals[idx]))
                {
                    return false;
                }
            }
            // check each of the last actual args to see if they can be one of varargs
            Class type = (formals[idx].isArray()) ? formals[idx].getComponentType() : formals[idx];
            for ( ; idx < actuals.length; idx++)
            {
                if (! isInstance(type, actuals[idx]))
                {
                    return false;
                }
            }
            // it was possible, and nothing else returned false, so
            return true;
        }
        // sizes don't match
        return false;
    }

    // if necessary, convert parameters into varArg array that Java expects
    private static Object[] convertArgs (Member function, 
                                         Class[] formals, Object[] actuals)
    {
        Object[] results = actuals;
        if (isVarArgs(function))
        {
            results = new Object[formals.length];
            int idx = 0;
            for ( ; idx < formals.length - 1; idx++)
            {
                // simply copy the basic parameters
                results[idx] = actuals[idx];
            }
            Class type = formals[idx].getComponentType();
            Object varArgs = Array.newInstance(type, actuals.length - formals.length + 1);
            for ( ; idx < actuals.length; idx++)
            {
                // fill the varArg array with the remaining parameters
                Array.set(varArgs, idx - formals.length + 1, actuals[idx]);
            }
            results[results.length - 1] = varArgs;
        }
        return results;
    }

    // Java should implement this correctly, but alas ...
    private static boolean isInstance (Class clss, Object instance)
    {
        final String TYPE = "TYPE";

        try
        {
            // handle primitives specially
            if (clss.isPrimitive())
            {
                Class thePrimitive = (Class)getFieldValue(instance, TYPE);
                if (! isAssignableFrom(clss, thePrimitive))
                {
                    // primitives are not exactly the same
                    return false;
                }
            }
            else if (! clss.isInstance(instance))
            {
                // not an instance of class or its sub-classes
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            // tried to compare primitive to non-primitive
            return false;
        }
    }

    // Java should implement this correctly, but alas ...
    // isVarArgs is a method of both constructors and methods, 
    //   but not to any of their common super-types
    private static boolean isVarArgs (Member function)
    {
        // BUGBUG: should call isVarArgs directly
        return Modifier.isTransient(function.getModifiers());
    }

    // Java should implement this correctly, but alas ...
    // right now, no added functionality, because of potential ambiguities
    private static boolean isAssignableFrom (Class formal, Class arg)
    {
        return formal.isAssignableFrom(arg);
    }

    // a possible future convenience function that converts 
    // an array of Objects to their corresponding Classes
    private static Class[] toClasses (Object[] args)
    {
        Class[] results = new Class[args.length];
        for (int k = 0; k < args.length; k++)
        {
            results[k] = args[k].getClass();
        }
        return results;
    }
}
