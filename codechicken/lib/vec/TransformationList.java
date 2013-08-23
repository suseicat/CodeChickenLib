package codechicken.lib.vec;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransformationList extends Transformation
{
    private ArrayList<Transformation> transformations = new ArrayList<Transformation>();
    private Matrix4 mat;
    
    public TransformationList(Transformation... transforms)
    {
        for(Transformation t : transforms)
            with(t);
    }
    
    public Matrix4 compile()
    {
        if(mat == null)
        {
            mat = new Matrix4();
            for(int i = transformations.size()-1; i >= 0; i--)
                transformations.get(i).apply(mat);
        }
        return mat;
    }
    
    /**
     * Returns a global space matrix as opposed to an object space matrix (reverse application order)
     * @return
     */
    public Matrix4 reverseCompile()
    {
        Matrix4 mat = new Matrix4();
        for(Transformation t : transformations)
            t.apply(mat);
        return mat;
    }
    
    @Override
    public void apply(Vector3 vec)
    {
        if(mat != null)
            mat.apply(vec);
        else
            for(Transformation t : transformations)
                t.apply(vec);
    }
    
    @Override
    public void applyN(Vector3 normal)
    {
        if(mat != null)
            mat.applyN(normal);
        else
            for(Transformation t : transformations)
                t.applyN(normal);
    }

    @Override
    public void apply(Matrix4 mat)
    {
        mat.multiply(compile());
    }
    
    @Override
    public TransformationList with(Transformation t)
    {
        mat = null;//matrix invalid
        if(t instanceof TransformationList)
            transformations.addAll(((TransformationList)t).transformations);
        else
            transformations.add(t);
        return this;
    }
    
    public TransformationList prepend(Transformation t)
    {
        mat = null;//matrix invalid
        if(t instanceof TransformationList)
            transformations.addAll(0, ((TransformationList)t).transformations);
        else
            transformations.add(0, t);
        return this;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void glApply()
    {
        for(int i = transformations.size()-1; i >= 0; i--)
            transformations.get(i).glApply();
    }
    
    @Override
    public Transformation inverse()
    {
        TransformationList rev = new TransformationList();
        for(int i = transformations.size()-1; i >= 0; i--)
            rev.with(transformations.get(i).inverse());
        return rev;
    }
    
    @Override
    public String toString()
    {
        String s = "";
        for(Transformation t : transformations)
            s+="\n"+t.toString();
        return s.trim();
    }
}
