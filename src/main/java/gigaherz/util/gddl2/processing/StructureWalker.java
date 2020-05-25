package gigaherz.util.gddl2.processing;

import gigaherz.util.gddl2.structure.Collection;
import gigaherz.util.gddl2.structure.Reference;
import gigaherz.util.gddl2.structure.Value;

public interface StructureWalker
{
    /**
     * Visits a value
     * @param v the value
     */
    void visitValue(Value v);

    /**
     * Visits a reference
     * @param r the reference
     */
    void visitReference(Reference r);

    /**
     * Visits a collection
     * @param c the collection
     * @return True if the children of this collection should be visited
     */
    boolean visitCollection(Collection c);
}
